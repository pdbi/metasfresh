package de.metas.handlingunits.picking.candidate.commands;

import java.util.List;
import java.util.Set;

import org.adempiere.ad.trx.api.ITrxManager;

import de.metas.handlingunits.HuPackingInstructionsId;
import de.metas.handlingunits.picking.PickingCandidate;
import de.metas.handlingunits.picking.PickingCandidateId;
import de.metas.handlingunits.picking.PickingCandidateRepository;
import de.metas.util.Check;
import de.metas.util.Services;
import lombok.Builder;
import lombok.NonNull;

/*
 * #%L
 * de.metas.handlingunits.base
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

public class SetHuPackingInstructionIdCommand
{
	private final ITrxManager trxManager = Services.get(ITrxManager.class);
	private final PickingCandidateRepository pickingCandidateRepository;

	private final Set<PickingCandidateId> pickingCandidateIds;
	private final HuPackingInstructionsId huPackingInstructionsId;

	@Builder
	private SetHuPackingInstructionIdCommand(
			@NonNull final PickingCandidateRepository pickingCandidateRepository,
			@NonNull final Set<PickingCandidateId> pickingCandidateIds,
			@NonNull final HuPackingInstructionsId huPackingInstructionsId)
	{
		Check.assumeNotEmpty(pickingCandidateIds, "pickingCandidateIds is not empty");

		this.pickingCandidateRepository = pickingCandidateRepository;
		this.pickingCandidateIds = pickingCandidateIds;
		this.huPackingInstructionsId = huPackingInstructionsId;
	}

	public List<PickingCandidate> perform()
	{
		return trxManager.callInThreadInheritedTrx(this::performInTrx);
	}

	private List<PickingCandidate> performInTrx()
	{
		final List<PickingCandidate> pickingCandidates = pickingCandidateRepository.getByIds(pickingCandidateIds);
		pickingCandidates.forEach(PickingCandidate::assertDraft);

		pickingCandidates.forEach(this::processPickingCandidate);

		return pickingCandidates;
	}

	private void processPickingCandidate(final PickingCandidate pickingCandidate)
	{
		pickingCandidate.changePackToInstructionsId(huPackingInstructionsId);
		pickingCandidateRepository.save(pickingCandidate);
	}
}
