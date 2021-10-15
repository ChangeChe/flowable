package com.github.changeche.flowable.task.job;

import com.github.changeche.flowable.model.consts.ModelTypeConst;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

/**
 * 模型激活 flowable-ui里创建的模型不能直接使用，需要激活
 * @author Chenjing
 */
@Configuration
@EnableScheduling
public class ModelActiveJob {

    @Autowired
    ModelService modelService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    FormRepositoryService formRepositoryService;

    /**
     * 激活流程定义
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void activeProcessDefinition() {
        // 查找BPMN模型
        List<AbstractModel> modelsByModelType = modelService.getModelsByModelType(ModelTypeConst.BPMN);
        for (AbstractModel model: modelsByModelType) {
            Date modelLastUpdated = model.getLastUpdated();
            // 查询该流程模型最新的发布记录
            List<Deployment> list = repositoryService.createDeploymentQuery().deploymentKey(model.getKey()).latest().list();
            boolean deploy = true;
            if (list.size() > 0) {
                Deployment deployment = list.get(0);
                Date deploymentTime = deployment.getDeploymentTime();
                // 如果模型的更新时间比最新的发布时间小，说明没有更新
                if (modelLastUpdated.compareTo(deploymentTime) < 0) {
                    deploy = false;
                }
            }
            // 发布
            if (deploy) {
                BpmnModel bpmnModel = modelService.getBpmnModel(model);
                repositoryService.createDeployment().name(model.getName()).key(model.getKey()).addBpmnModel(model.getKey() + ".bpmn", bpmnModel).deploy();
            }
        }
    }

    /**
     * 激活表单
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void activeFormDefinition() {
        // 查找FORM模型
        List<AbstractModel> modelsByModelType = modelService.getModelsByModelType(ModelTypeConst.FORM);
        for (AbstractModel model: modelsByModelType) {
            Date modelLastUpdated = model.getLastUpdated();
            List<FormDeployment> list = formRepositoryService.createDeploymentQuery().formDefinitionKey(model.getKey()).list();

            boolean deploy = true;
            if (list.size() > 0) {
                list.sort((o1, o2) -> o2.getDeploymentTime().compareTo(o1.getDeploymentTime()));
                // 查询该表单模型最新的发布记录
                FormDeployment formDeployment = list.get(0);
                Date deploymentTime = formDeployment.getDeploymentTime();
                // 如果模型的更新时间比最新的发布时间小，说明没有更新
                if (modelLastUpdated.compareTo(deploymentTime) < 0) {
                    deploy = false;
                }
            }
            // 发布
            if (deploy) {
                formRepositoryService.createDeployment().name(model.getName()).addFormDefinition(model.getKey() + ".form", model.getModelEditorJson()).deploy();
            }
        }
    }
}
