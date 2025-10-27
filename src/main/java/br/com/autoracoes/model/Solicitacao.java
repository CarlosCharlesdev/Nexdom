package br.com.autoracoes.model;

import java.time.LocalDateTime;

public class Solicitacao {
    private Long id;
    private String pacienteNome;
    private String pacienteSexo;
    private Integer pacienteIdade;
    private String procedimentoCodigo;
    private LocalDateTime dataSolicitacao;
    private Boolean autorizado;
    private String justificativa;

    public Solicitacao() {
        this.dataSolicitacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPacienteNome() { return pacienteNome; }
    public void setPacienteNome(String pacienteNome) { this.pacienteNome = pacienteNome; }
    public String getPacienteSexo() { return pacienteSexo; }
    public void setPacienteSexo(String pacienteSexo) { this.pacienteSexo = pacienteSexo; }
    public Integer getPacienteIdade() { return pacienteIdade; }
    public void setPacienteIdade(Integer pacienteIdade) { this.pacienteIdade = pacienteIdade; }
    public String getProcedimentoCodigo() { return procedimentoCodigo; }
    public void setProcedimentoCodigo(String procedimentoCodigo) { this.procedimentoCodigo = procedimentoCodigo; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public Boolean getAutorizado() { return autorizado; }
    public void setAutorizado(Boolean autorizado) { this.autorizado = autorizado; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}
