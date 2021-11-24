package com.example.timdetask.job;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component("job")
public class TestJob {

    public void NoParams()
    {
        System.out.println(new Date()+"执行无参方法");
    }

    public void NoParams2()
    {
        System.out.println(new Date()+"执行无参方法2");
    }

    public void Params(String params)
    {
        System.out.println("执行有参方法：" + params);
    }



}
