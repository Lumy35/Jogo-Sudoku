package main.model;

public class Space {
    private Integer atual;
    private final int esperado;
    private final boolean fixo;

    public Space(int esperado, boolean fixo) {
        this.esperado = esperado;
        this.fixo = fixo;
        if (fixo) {
            atual = esperado;
        }
    }

    public Integer getAtual() {
        return atual;
    }

    public int getEsperado() {
        return esperado;
    }

    public boolean isFixo() {
        return fixo;
    }

    public void setAtual(Integer atual) {
        if (fixo) return;
        this.atual = atual;
    }

    public void clearSpace() {
        setAtual(null);
    }
}
