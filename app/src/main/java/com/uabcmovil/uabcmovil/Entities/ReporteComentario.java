package com.uabcmovil.uabcmovil.Entities;

import java.util.UUID;

public class ReporteComentario {
    private String usuarioReporto = "";
    private String usuarioReportado = "";
    private String comentarioReportado = "";
    private String comentarioReportadoKey = "";
    private String keyUsuarioReporto = "";
    private String keyUsuarioReportado = "";
    private String keyReporte = UUID.randomUUID().toString().replaceAll("-","");

    public ReporteComentario(){}

    public String getUsuarioReporto() {
        return usuarioReporto;
    }

    public void setUsuarioReporto(String usuarioReporto) {
        this.usuarioReporto = usuarioReporto;
    }

    public String getUsuarioReportado() {
        return usuarioReportado;
    }

    public void setUsuarioReportado(String usuarioReportado) {
        this.usuarioReportado = usuarioReportado;
    }

    public String getComentarioReportado() {
        return comentarioReportado;
    }

    public void setComentarioReportado(String comentarioReportado) {
        this.comentarioReportado = comentarioReportado;
    }

    public String getKeyUsuarioReporto() {
        return keyUsuarioReporto;
    }

    public void setKeyUsuarioReporto(String keyUsuarioReporto) {
        this.keyUsuarioReporto = keyUsuarioReporto;
    }

    public String getKeyReporte() {
        return keyReporte;
    }

    public void setKeyReporte(String keyReporte) {
        this.keyReporte = keyReporte;
    }

    public String getKeyUsuarioReportado() {
        return keyUsuarioReportado;
    }

    public void setKeyUsuarioReportado(String keyUsuarioReportado) {
        this.keyUsuarioReportado = keyUsuarioReportado;
    }

    public String getComentarioReportadoKey() {
        return comentarioReportadoKey;
    }

    public void setComentarioReportadoKey(String comentarioReportadoKey) {
        this.comentarioReportadoKey = comentarioReportadoKey;
    }
}
