package de.paladinsinn.tp.dcis.model.missions;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;

import java.util.UUID;

/**
 * @author rlichti
 * @version 2.0.0 2021-09-08
 * @since 2.0.0 2021-09-08
 */
public interface MissionResource extends PanacheRepositoryResource<MissionRepository, Mission, UUID> {
}
