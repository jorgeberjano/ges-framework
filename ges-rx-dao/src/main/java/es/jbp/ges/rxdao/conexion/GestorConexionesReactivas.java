package es.jbp.ges.rxdao.conexion;

import es.jbp.comun.utiles.sql.compatibilidad.CompatibilidadSql;
import es.jbp.comun.utiles.sql.compatibilidad.FormateadorSql;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.reactivestreams.Publisher;

import java.util.Optional;

/**
 * @author jorge
 */
public class GestorConexionesReactivas {

    private final String driver;
    private final String host;
    private final String database;
    private final String user;
    private final String password;

    private ConnectionFactory connectionFactory;

    public GestorConexionesReactivas(String driver, String host, String database, String user, String password) {
        this.driver = driver;
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public Publisher<? extends Connection> getConexion() {

        if (connectionFactory == null) {
            ConnectionFactoryOptions options
                    = ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, driver)
                    .option(ConnectionFactoryOptions.HOST, host)
                    .option(ConnectionFactoryOptions.DATABASE, database)
                    .option(ConnectionFactoryOptions.USER, user)
                    .option(ConnectionFactoryOptions.PASSWORD, password)
                    .build();
            connectionFactory = ConnectionFactories.find(options);
        }
        return Optional.ofNullable(connectionFactory)
                .map(ConnectionFactory::create)
                .orElse(null);
    }

    public FormateadorSql getFormateadorSql() {
        return CompatibilidadSql.getFormateador(driver);
    }
}
