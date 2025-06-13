package it.unimib.sd2025;

import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.json.bind.JsonbBuilder;

@Path("buono")
public class BuonoResource {


    @POST
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVoucher(@PathParam("cf") String cf) {
        try {
            

            return Response.status(Status.CREATED)
                         .build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                         .entity("Error creating voucher")
                         .build();
        }
    }
}