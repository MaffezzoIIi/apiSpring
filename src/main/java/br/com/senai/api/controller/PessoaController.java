package br.com.senai.api.controller;

import br.com.senai.api.assembler.PessoaAssembler;
import br.com.senai.api.model.PessoaDTO;
import br.com.senai.api.model.input.PessoaInputDTO;
import br.com.senai.domain.model.Pessoa;
import br.com.senai.domain.repository.PessoaRepository;
import br.com.senai.domain.service.PessoaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private PessoaRepository pessoaRepository;
    private PessoaService pessoaService;
    private PessoaAssembler pessoaAssembler;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaDTO cadastrar(@Valid @RequestBody PessoaInputDTO pessoaInputDTO){
        Pessoa novaPessoa = pessoaAssembler.toEntity(pessoaInputDTO);
        novaPessoa.getUsuario().setSenha(
                new BCryptPasswordEncoder().encode(pessoaInputDTO.getUsuario().getSenha())
        );
        Pessoa pessoa = pessoaService.cadastrar(novaPessoa);

        return pessoaAssembler.toModel(pessoa);
    }


    @GetMapping
    public List<PessoaDTO> listar(){
        return pessoaService.listar();
    }


    @GetMapping("/buscar/{pessoaNome}")
    public List<PessoaDTO> listarPorNome(@PathVariable String pessoaNome){
        return pessoaService.listarPorNome(pessoaNome);
    }


    @GetMapping("/buscar/containing/{nomeContaining}")
    public List<PessoaDTO> listarNomeContaining(@PathVariable String nomeContaining){
        return pessoaService.listarNomeQueContem(nomeContaining);
    }


    @GetMapping("/buscar/byId/{pessoaId}")
    public ResponseEntity<PessoaDTO> buscar(@PathVariable long pessoaId){
        return pessoaService.buscarPorId(pessoaId);
    }


    @PutMapping("/editar/{pessoaId}")
    public ResponseEntity<PessoaDTO> editar(@Valid @PathVariable Long pessoaId, @RequestBody PessoaInputDTO pessoaInput){

        if (!pessoaRepository.existsById(pessoaId)){
            return  ResponseEntity.notFound().build();
        }

        Pessoa novaPessoa = pessoaAssembler.toEntity(pessoaInput);
        Pessoa pessoa = pessoaService.editar(pessoaId, novaPessoa);

        return ResponseEntity.ok(pessoaAssembler.toModel(pessoa));
    }


    @DeleteMapping("/{pessoaId}")
    public ResponseEntity<Pessoa> remover(@PathVariable Long pessoaId){
        if (!pessoaRepository.existsById(pessoaId)){
            return  ResponseEntity.notFound().build();
        }
        pessoaService.remover(pessoaId);

        return ResponseEntity.noContent().build();
    }
}
