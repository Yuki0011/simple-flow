package com.sentury.approvalflow.config;

import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.utils.ReflectionsCache;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用启动完成
 */
@Component
@Slf4j
public class ApplicationStartUpListener implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        String packageName = Node.class.getPackage().getName();

        Reflections reflections = new Reflections(packageName);
        ReflectionsCache.setReflections(reflections);
        log.info("应用启动完成");


    }



}
