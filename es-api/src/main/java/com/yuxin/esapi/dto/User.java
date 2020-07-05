package com.yuxin.esapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author boyuxin
 * @description
 * @date 2020/7/5 9:49
 */
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public String  name;
    public  int age;

}
