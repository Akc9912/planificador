package aktech.planificador.DTO.auth;

public class ChangePasswordResponseDTO {
    private String message;
    private boolean success;

    public ChangePasswordResponseDTO() {
    }

    public ChangePasswordResponseDTO(String message, boolean success) {
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
