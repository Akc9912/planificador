package aktech.planificador.Dto.auth;

public class ChangePasswordResponseDto {
    private String message;
    private boolean success;

    public ChangePasswordResponseDto() {
    }

    public ChangePasswordResponseDto(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
