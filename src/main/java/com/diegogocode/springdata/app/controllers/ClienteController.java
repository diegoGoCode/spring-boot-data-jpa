package com.diegogocode.springdata.app.controllers;

import com.diegogocode.springdata.app.entity.Cliente;
import com.diegogocode.springdata.app.services.IClienteService;
import com.diegogocode.springdata.app.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    private static final String listarClientes = "listar";
    private static final String formularioCliente = "form";

    @Autowired
    private IClienteService iClienteService;

    @GetMapping(value = "/clientes")
    public String listarClientes(@RequestParam(name = "page", defaultValue = "0") int page, Model model){
        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = iClienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);

        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
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
    public String guardarNuevoCliente(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flas, SessionStatus status){
        if(result.hasErrors()){
            model.addAttribute("titulo", "Formulario nuevo cliente");
            return "form";
        }

        String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con exito!" : "Cliente creado con exito!";
        iClienteService.save(cliente);
        status.setComplete();
        flas.addFlashAttribute("success", mensajeFlash);
        return "redirect:clientes";
    }

    @GetMapping("/form/{id}")
    public String actualizarCliente(@PathVariable Long id, Map<String, Object> model, RedirectAttributes flash){
        Cliente cliente = null;
        if(id > 0){
            cliente = iClienteService.findOne(id);
            if(cliente == null){
                flash.addFlashAttribute("error", "El cliente con el ID: ".concat(id.toString()).concat(" no existe en la base de datos"));
                return "redirect:/clientes";
            }
        }else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
            return "redirect:/clientes";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar Cliente");
        return ClienteController.formularioCliente;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes flash){
        if(id > 0){
            iClienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con exito!");
        }
        return "redirect:/clientes";
    }

}
