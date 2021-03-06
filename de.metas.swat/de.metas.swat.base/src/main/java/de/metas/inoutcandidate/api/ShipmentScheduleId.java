package de.metas.inoutcandidate.api;

import java.util.Collection;

import org.adempiere.util.lang.impl.TableRecordReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableSet;

import de.metas.inoutcandidate.model.I_M_ShipmentSchedule;
import de.metas.lang.RepoIdAware;
import de.metas.util.Check;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * de.metas.swat.base
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Value
public class ShipmentScheduleId implements RepoIdAware
{
	@JsonCreator
	public static ShipmentScheduleId ofRepoId(final int repoId)
	{
		return new ShipmentScheduleId(repoId);
	}

	public static ShipmentScheduleId ofRepoIdOrNull(final int repoId)
	{
		return repoId > 0 ? ofRepoId(repoId) : null;
	}

	public static ImmutableSet<Integer> toIntSet(@NonNull final Collection<ShipmentScheduleId> ids)
	{
		if (ids.isEmpty())
		{
			return ImmutableSet.of();
		}

		return ids.stream().map(ShipmentScheduleId::getRepoId).collect(ImmutableSet.toImmutableSet());
	}

	int repoId;

	private ShipmentScheduleId(final int shipmentScheduleRepoId)
	{
		this.repoId = Check.assumeGreaterThanZero(shipmentScheduleRepoId, "shipmentScheduleRepoId");
	}

	@Override
	@JsonValue
	public int getRepoId()
	{
		return repoId;
	}

	public static int toRepoId(final ShipmentScheduleId id)
	{
		return id != null ? id.getRepoId() : -1;
	}

	public TableRecordReference toTableRecordReference()
	{
		return TableRecordReference.of(I_M_ShipmentSchedule.Table_Name, getRepoId());
	}
}
