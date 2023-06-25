package com.mjd507.validate;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Created by mjd on 2021/5/19 21:34
 */
@RestController
@RequestMapping("/valid")
public class ValidateController {

    @PostMapping("")
    public String validParam(@RequestBody @Valid ReqVo reqVo) {
        return "ok";
    }
}
