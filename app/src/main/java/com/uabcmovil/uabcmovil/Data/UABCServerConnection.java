package com.uabcmovil.uabcmovil.Data;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Entities.Alumno;
import com.uabcmovil.uabcmovil.Entities.Materia;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

    public class UABCServerConnection extends AsyncTask {
        static Document page;

        public UABCServerConnection() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("COMENZO","LA CONEXION");

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("TERMINO","LA CONEXION");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            switch ((String) objects[0]) {
                case "crearConexion":
                    return CreateConnection((String) objects[1], (String) objects[2]);
                case "getUserInfo":
                    return GetUserInfo((Connection.Response) objects[1]);
            }
            return null;
        }

        private Alumno GetUserInfo(Connection.Response con) {
            try {
                page = Jsoup.connect("http://alumnos.uabc.mx/group/alumnos/horario")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                        .cookies(con.cookies())
                        .timeout(10 * 1000)
                        .get();
                Alumno usuario = new Alumno();
                Log.e("SITIO", page.location());
                Element elemento = page.getElementById("_17_WAR_piahorario_INSTANCE_hswF4Pf98uyS_:j_idt5:nombreCompleto");
                usuario.setNombre(elemento.text());
                elemento = page.getElementById("_17_WAR_piahorario_INSTANCE_hswF4Pf98uyS_:j_idt5:matricula");
                usuario.setMatricula(elemento.text());
                elemento = page.getElementById("_17_WAR_piahorario_INSTANCE_hswF4Pf98uyS_:j_idt5:carrera");
                usuario.setCarrera(elemento.text());

                ArrayList<Materia> clases = new ArrayList<>();
                Elements elementos = page.getElementsByClass("titulo");
                Elements elementosNext = elementos.next();
                int limitExcludedHeaders = 5, currentItem = 0;
                Materia clase = new Materia();
                for (int i = 0; i < elementosNext.size(); i++) {
                    if (i > limitExcludedHeaders) {
                        switch (currentItem) {
                            case 0:
                                clase.setNombreClase(elementosNext.get(i).text());
                                break;
                            case 1:
                                break;
                            case 2:
                                clase.setProfesor(elementosNext.get(i).text());
                                break;
                            case 3:
                                clase.setGrupo(elementosNext.get(i).text());
                                break;
                            case 4:
                                clase.setSubGrupo(elementosNext.get(i).text());
                                break;
                            case 5:
                                clase.setSalon(elementosNext.get(i).text());
                                break;
                            case 6:
                                clase.setEtapa(elementosNext.get(i).text());
                                break;
                            case 7:
                                clase.setTipoClase(elementosNext.get(i).text());
                                clases.add(clase);
                                clase = new Materia();
                                currentItem = -1;
                                break;
                        }
                        currentItem++;
                    }
                }
                Elements nextNextElements = elementosNext.next();
                currentItem = 0;
                for (int n = 0; n < nextNextElements.size(); n++) {
                    if (nextNextElements.get(n).text().contains("Día")) {
                        clases.get(currentItem).setHora(nextNextElements.get(n).text().substring(11));
                        currentItem++;
                    }
                }
                usuario.setClases(clases);
                return usuario;
            } catch (IOException e1) {
                Log.e("ERROR", "NO SE PUDO CONECTAR");
            }
            return null;
        }

        private Object CreateConnection(String object, String object1) {
            try {
                Connection.Response conexionInicial = Jsoup.connect("http://alumnos.uabc.mx/web/alumnos/login")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                        .method(Connection.Method.GET)
                        .execute();

                Connection.Response login = Jsoup.connect("http://alumnos.uabc.mx/web/alumnos/login?p_p_id=58&p_p_lifecycle=1&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_58_struts_action=%2Flogin%2Flogin")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                        .data("_58_login", object)
                        .data("_58_password", object1)
                        .cookies(conexionInicial.cookies())
                        .method(Connection.Method.POST)
                        .execute();

                Document page = Jsoup.connect("http://alumnos.uabc.mx/group/alumnos/horario")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                        .cookies(login.cookies())
                        .timeout(10 * 1000)
                        .get();

                if (page.location().equals("http://alumnos.uabc.mx/group/alumnos/horario")) {
                    Log.e("page", "http://alumnos.uabc.mx/group/alumnos/horario");
                    return login;
                }
            } catch (IOException ex) {
                Log.e("ERROR", ex.getMessage());
            }
            return null;
        }
    }
