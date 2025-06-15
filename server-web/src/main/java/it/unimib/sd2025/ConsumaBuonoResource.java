package it.unimib.sd2025;

import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("consuma-buono")
public class ConsumaBuonoResource {
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consuma(@PathParam("id") String id) {
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            String response = buonoRepository.consumaBuono(id);

            if (response.startsWith("ERROR")) {
                return Response.status(Status.NOT_FOUND)
                        .entity(response)
                        .build();
            }

            return Response.ok(response)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: ")
                    .build();
        }
    }
}