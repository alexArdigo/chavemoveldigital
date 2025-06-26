package pt.gov.chavemoveldigital.models;

public class CodeDTO {

    private Integer code;
    private int delay;

    public CodeDTO(Integer code, int delay) {
        this.delay = delay;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
