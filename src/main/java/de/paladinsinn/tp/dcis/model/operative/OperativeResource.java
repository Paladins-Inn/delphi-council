package de.paladinsinn.tp.dcis.model.operative;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;

import java.util.UUID;

/**
 * @author rlichti
 * @version 1.0.0 2021-09-08
 * @since 1.0.0 2021-09-08
 */
public interface OperativeResource extends PanacheRepositoryResource<OperativeRepository, Operative, UUID> {
}
