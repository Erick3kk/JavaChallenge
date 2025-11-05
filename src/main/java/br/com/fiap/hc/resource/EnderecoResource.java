package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.EnderecoDao;
import br.com.fiap.hc.dto.endereco.AtualizarEnderecoDto;
import br.com.fiap.hc.dto.endereco.CadastroEnderecoDto;
import br.com.fiap.hc.dto.endereco.DetalhesEnderecoDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Endereco;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/enderecos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnderecoResource {

    @Inject
    private EnderecoDao enderecoDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        enderecoDao.deletar(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, @Valid AtualizarEnderecoDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Endereco endereco = modelMapper.map(dto, Endereco.class);
        endereco.setIdEndereco(id);
        enderecoDao.atualizar(endereco);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesEnderecoDto dto = modelMapper.map(enderecoDao.buscar(id), DetalhesEnderecoDto.class);
        return Response.ok(dto).build();
    }

    @GET
    public List<DetalhesEnderecoDto> listar() throws SQLException {
        return enderecoDao.listar().stream()
                .map(e -> modelMapper.map(e, DetalhesEnderecoDto.class))
                .toList();
    }

    @POST
    public Response create(@Valid CadastroEnderecoDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        Endereco endereco = modelMapper.map(dto, Endereco.class);
        enderecoDao.cadastrar(endereco);

        // Constrói a URL para o recurso criado
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(endereco.getIdEndereco()))
                .build();

        return Response.created(uri)
                .entity(modelMapper.map(endereco, DetalhesEnderecoDto.class))
                .build(); // 201 Created
    }
}