package com.example.course_Login.service;

import com.example.course_Login.entities.RedeSocial;
import com.example.course_Login.entities.Telefone;
import com.example.course_Login.entities.Usuario;
import com.example.course_Login.repositories.RedeSocialRepository;
import com.example.course_Login.repositories.UsuarioRepository;
import com.example.course_Login.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RedeSocialService {
    @Autowired
    private RedeSocialRepository redeSocialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RedeSocial> findAll() {
        return redeSocialRepository.findAll();
    }


    public List<RedeSocial> findBySocial(String obj) {
        List<RedeSocial> redeSocialLista = redeSocialRepository.findAll();
        List<RedeSocial> novaListaSocial = new ArrayList<>();

        if (redeSocialLista.isEmpty()) {
            throw new NaoEncontradoIdException(obj);
        }

        for (RedeSocial redeSocial : redeSocialLista) {
            if (obj.equals(redeSocial.getMidia())) {
                novaListaSocial.add(redeSocial);
            }
        }

        return novaListaSocial;
    }

    public boolean findId(String obj) {
        List<RedeSocial> redeSocials = redeSocialRepository.findAll();
        List<RedeSocial> novaListSocial = new ArrayList<>();
        boolean encontrado = false;

        if (redeSocials.isEmpty()) {
            throw new NaoEncontradoIdException(obj);
        }

        for (RedeSocial redeSocial : redeSocials) {
            if (obj.equals(redeSocial.getMidia())) {
                novaListSocial.add(redeSocial);
                encontrado = true;
            }
        }
        return encontrado;
    }

    public List<RedeSocial> insertRedeSocialMultiples(List<RedeSocial> bodyRedeSocial, Long usuarioId) {

        Optional<Usuario> usuarioIdBanco = usuarioRepository.findById(usuarioId);
        Usuario usuarioSet = usuarioIdBanco.orElseThrow(() -> new NaoEncontradoIdException(usuarioId));
        usuarioSet = usuarioIdBanco.get();

        List<RedeSocial> listaString = new ArrayList<>();
        Usuario redeSocialNaLista = usuarioIdBanco.get();
        Set<RedeSocial> listaSocialBanco = redeSocialNaLista.getRedeSocialList();
        List<RedeSocial> newList = new ArrayList<>();
        boolean encontrado = false;

        for (RedeSocial body2 : bodyRedeSocial) {
            if (newList.isEmpty()) {
                newList.add(body2);
            } else {
                for (RedeSocial listaCopiaSocial : newList) {
                    if (body2.getMidia().equals(listaCopiaSocial.getMidia())) {
                        throw new ExistenteRedeSocialException(listaCopiaSocial.getMidia());
                    }
                }
                newList.add(body2);
            }
        }

        for (RedeSocial body : bodyRedeSocial) {
            for (RedeSocial bancoRede : listaSocialBanco) {
                if (bancoRede.getMidia().equals(body.getMidia())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                listaString.add(body);
                listaSocialBanco.add(body);
                body.setUsuario(usuarioSet);
            }
            encontrado = false;
        }

        return redeSocialRepository.saveAll(listaString);
    }

    public List<RedeSocial> update(Long id, List<RedeSocial> bodyRedeSocial) {

        Optional<Usuario> usuarioId = usuarioRepository.findById(id);
        usuarioId.orElseThrow(() -> new NaoEncontradoIdException(id));
        Usuario redeSocialNaLista = usuarioId.get();
        Set<RedeSocial> listaSocialBanco = redeSocialNaLista.getRedeSocialList();

        for (RedeSocial body : bodyRedeSocial) {
            for (RedeSocial bancoSocial : listaSocialBanco) {
                if (body.getMidia().equals(bancoSocial.getMidia())) {
                    if (!body.getLinkRede().equals(bancoSocial.getLinkRede()) && !body.getLinkRede().isBlank()) {
                        if (body.getLinkRede().contains("@")){
                            bancoSocial.setLinkRede(body.getLinkRede());
                        }
                    }
                }
            }
        }
        return redeSocialRepository.saveAll(listaSocialBanco);
    }

}
