package com.jiandong.validate;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Created by mjd on 2021/5/19 21:35
 */
@GroupSequence({ ReqVoBase.class, ReqVo.class, CombinedCheck.class})
public class ReqVo extends ReqVoBase {

    @AssertTrue(message = "invalid name", groups = CombinedCheck.class)
    private boolean isValidName() {
        return getName().equals("abc");
    }

    @AssertTrue(message = "all values must greater than 10", groups = CombinedCheck.class)
    private boolean isValidNum() {
        return getNums().stream().allMatch(integer -> integer > 10);
    }
}
interface CombinedCheck{}