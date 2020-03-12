package com.ts.graphql.gateway;

import java.io.Reader;
import java.net.MalformedURLException;
import java.util.function.Supplier;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.google.gson.Gson;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

@Controller
public class GraphQLController {
    private final SchemaNamespace BASIC_NODE = SchemaNamespace.of("graphqlbasics");
    private final String BASICS_SCHEMA_URL = "http://localhost:8443/graphql";

    private final SchemaNamespace NODE2 = SchemaNamespace.of("graphql-node2");
    private final String NODE2_SCHEMA_URL = "http://localhost:8444/graphql";

    Supplier<Reader> usersSchemaProvider = () -> new RemoteIntrospection(BASICS_SCHEMA_URL).get();
    Supplier<Reader> ordersSchemaProvider = () -> new RemoteIntrospection(NODE2_SCHEMA_URL).get();

    Braid braid = Braid
        .builder()
        .schemaSource(
            QueryExecutorSchemaSource
                .builder()
                .namespace(BASIC_NODE)
                .schemaProvider(usersSchemaProvider)
                .remoteRetriever(new RemoteRetriever<Object>(BASICS_SCHEMA_URL))
                .build())
        .schemaSource(
            QueryExecutorSchemaSource
                .builder()
                .namespace(NODE2)
                .schemaProvider(ordersSchemaProvider)
                .remoteRetriever(new RemoteRetriever<Object>(NODE2_SCHEMA_URL))
                //.links(links)
                .build())
        .build();


    @PostMapping(path="/graphql", consumes="application/json", produces="application/json")
    public @ResponseBody
    String graphql(@RequestBody GraphQLParameters params) throws MalformedURLException {
        Gson gson = new Gson();

        BraidGraphQL graphql = braid.newGraphQL();

        ExecutionResult result = graphql
            .execute(
                ExecutionInput
                    .newExecutionInput()
                    .query(params.getQuery())
                    .build())
            .join();

        return gson.toJson(result.toSpecification());
    }
}