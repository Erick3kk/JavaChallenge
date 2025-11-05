package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.MedicoDao;
import br.com.fiap.hc.dto.medico.AtualizarMedicoDto;
import br.com.fiap.hc.dto.medico.CadastroMedicoDto;
import br.com.fiap.hc.dto.medico.DetalhesMedicoDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Medico;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/medicos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicoResource {

    @Inject
    private MedicoDao medicoDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        medicoDao.deleteMedico(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, @Valid AtualizarMedicoDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Medico medico = modelMapper.map(dto, Medico.class);
        medico.setIdMedico(id);
        medicoDao.atualizarMedico(medico);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesMedicoDto dto = modelMapper.map(medicoDao.buscar(id), DetalhesMedicoDto.class);
        return Response.ok(dto).build();
    }

    @GET
    public List<DetalhesMedicoDto> listar() throws SQLException {
        return medicoDao.listarMedicos().stream()
                .map(m -> modelMapper.map(m, DetalhesMedicoDto.class))
                .toList();
    }

    @POST
    public Response create(@Valid CadastroMedicoDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        Medico medico = modelMapper.map(dto, Medico.class);
        medicoDao.cadastrarMedico(medico);

        // Constrói a URL para o recurso criado
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(medico.getIdMedico()))
                .build();

        return Response.created(uri)
                .entity(modelMapper.map(medico, DetalhesMedicoDto.class))
                .build(); // 201 Created
    }
}