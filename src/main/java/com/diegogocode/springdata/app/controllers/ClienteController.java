package com.diegogocode.springdata.app.controllers;

import com.diegogocode.springdata.app.entity.Cliente;
import com.diegogocode.springdata.app.services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    private static final String listarClientes = "listar";
    private static final String formularioCliente = "form";

    @Autowired
    private IClienteService iClienteService;

    @GetMapping(value = {"/","/clientes"})
    public String listarClientes(Model model){
        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("clientes", iClienteService.findAll());
        return ClienteController.listarClientes;
    }

    @GetMapping(value = "/form")
    public String mostrarFormularioCliente(Map<String, Object> model){
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario nuevo cliente");
        return ClienteController.formularioCliente;
    }

    @PostMapping(value = "/form")
    public String guardarNuevoCliente(@Valid Cliente cliente, BindingResult result, Model model, SessionStatus status){
        if(result.hasErrors()){
            model.addAttribute("titulo", "Formulario nuevo cliente");
            return "form";
        }
        iClienteService.save(cliente);
        status.setComplete();
        return "redirect:clientes";
    }

    @GetMapping("/form/{id}")
    public String actualizarCliente(@PathVariable Long id, Map<String, Object> model){
        Cliente cliente = null;
        if(id>0){
            cliente = iClienteService.findOne(id);
        }else {
            return "redirect:clientes";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar Cliente");
        return ClienteController.formularioCliente;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id){
        if(id > 0){
            iClienteService.delete(id);
        }
        return "redirect:/clientes";
    }

}
