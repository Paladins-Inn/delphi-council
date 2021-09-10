package de.paladinsinn.tp.dcis.model.specialmissions;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

import java.util.UUID;

/**
 * @author rlichti
 * @version 2.0.0 2021-09-08
 * @since 2.0.0 2021-09-08
 */
@ResourceProperties(
        path = "/api/v1/specialmissions",
        paged = true,
        hal = true, halCollectionName = "specialmissions"
)
public interface SpecialMissionResource extends PanacheRepositoryResource<SpecialMissionRepository, SpecialMission, UUID> {
}
