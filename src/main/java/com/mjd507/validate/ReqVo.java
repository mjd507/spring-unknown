package com.mjd507.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Created by mjd on 2021/5/19 21:35
 */
public class ReqVo {

    @NotNull(message = "name must have val")
    private String name;

    @Size(min = 1, max = 3)
    private List<Integer> nums;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getNums() {
        return nums;
    }

    public void setNums(List<Integer> nums) {
        this.nums = nums;
    }
}
