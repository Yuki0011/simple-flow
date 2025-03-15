package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.common.config.NotWriteLogAnno;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


public class WebController {

    @RequestMapping("/")
    @NotWriteLogAnno(printResultLog = false)
    @SneakyThrows
    public void index(HttpServletResponse response){
        ClassPathResource classPathResource=new ClassPathResource("web/index.html");

        String s = FileUtil.readUtf8String(classPathResource.getPath());

        IoUtil.writeUtf8(response.getOutputStream(),true,s);
    }


    @NotWriteLogAnno(printResultLog = false)
    @RequestMapping("{a}")
    @SneakyThrows
    public void f(HttpServletResponse response, @PathVariable String a){


        ClassPathResource classPathResource=new ClassPathResource("web/"+a);

        byte[] s = FileUtil.readBytes(classPathResource.getPath());

        IoUtil.write(response.getOutputStream(),true,s);
    }

    @NotWriteLogAnno(printResultLog = false)
    @RequestMapping("{js}/{a}.js")
    @SneakyThrows
    public void js(HttpServletResponse response,@PathVariable String js, @PathVariable String a){

        ClassPathResource classPathResource=new ClassPathResource("web/"+js+"/"+a+".js");

        byte[] s = FileUtil.readBytes(classPathResource.getPath());
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("application/javascript");
        IoUtil.write(outputStream,true,s);
    }

    @NotWriteLogAnno(printResultLog = false)
    @RequestMapping("{js}/{a}")
    @SneakyThrows
    public void jscss(HttpServletResponse response,@PathVariable String js, @PathVariable String a){

        ClassPathResource classPathResource=new ClassPathResource("web/"+js+"/"+a);

        byte[] s = FileUtil.readBytes(classPathResource.getPath());
        IoUtil.write(response.getOutputStream(),true,s);
    }

}
