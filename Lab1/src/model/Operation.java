package model;


public class Operation {
    private int id;
    private String sign;
    private int sum;

    Operation(int id, String sign, int sum) {
        this.id = id;
        this.sign = sign;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", sign='" + sign + '\'' +
                ", sum=" + sum +
                '}';
    }
}
