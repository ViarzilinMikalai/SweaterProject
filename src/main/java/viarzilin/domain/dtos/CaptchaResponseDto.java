package viarzilin.domain.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.Set;
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaResponseDto {
    public boolean success;
    @JsonAlias("error-codes")
    public Set<String> errorCodes;

//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//
//    public Set<String> getErrorCodes() {
//        return errorCodes;
//    }
//
//    public void setErrorCodes(Set<String> errorCodes) {
//        this.errorCodes = errorCodes;
//    }
}
