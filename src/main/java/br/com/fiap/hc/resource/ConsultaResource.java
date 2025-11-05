package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.ConsultaDao;
import br.com.fiap.hc.dto.consulta.AtualizarConsultaDto;
import br.com.fiap.hc.dto.consulta.CadastroConsultaDto;
import br.com.fiap.hc.dto.consulta.DetalhesConsultaDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaResource {

    @Inject
    private ConsultaDao consultaDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        consultaDao.deletar(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, @Valid AtualizarConsultaDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Consulta consulta = modelMapper.map(dto, Consulta.class);
        consulta.setIdConsulta(id);
        consultaDao.atualizar(consulta);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesConsultaDto dto = modelMapper.map(consultaDao.buscar(id), DetalhesConsultaDto.class);
        return Response.ok(dto).build();
    }

    @GET
    public List<DetalhesConsultaDto> listar() throws SQLException {
        return consultaDao.listarConsulta().stream()
                .map(c -> modelMapper.map(c, DetalhesConsultaDto.class))
                .toList();
    }

    @POST
    public Response create(@Valid CadastroConsultaDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        Consulta consulta = modelMapper.map(dto, Consulta.class);
        consultaDao.cadastrar(consulta);

        // Constrói a URL para o recurso criado
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(consulta.getIdConsulta()))
                .build();

        return Response.created(uri)
                .entity(modelMapper.map(consulta, DetalhesConsultaDto.class))
                .build(); // 201 Created
    }
}