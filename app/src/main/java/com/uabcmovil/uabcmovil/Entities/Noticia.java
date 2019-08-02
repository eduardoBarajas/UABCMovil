package com.uabcmovil.uabcmovil.Entities;

public class Noticia {
        private String noticia = "null", titulo = "null", categoria = "null", fecha = "null", key = "null", imagen = "null";
        public Noticia(){}
        public Noticia(String n, String t, String c, String f ){
            noticia = n;
            titulo = t;
            categoria = c;
            fecha = f;
            imagen = "null";
            key = "null";
        }

        public String getNoticia() {
            return noticia;
        }

        public void setNoticia(String noticia) {
            this.noticia = noticia;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getImagen() {
            return imagen;
        }

        public void setImagen(String imagen) {
            this.imagen = imagen;
        }
    }