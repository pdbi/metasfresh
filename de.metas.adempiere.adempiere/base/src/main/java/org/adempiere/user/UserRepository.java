package org.adempiere.user;

import static org.adempiere.model.InterfaceWrapperHelper.load;
import static org.adempiere.model.InterfaceWrapperHelper.loadOutOfTrx;
import static org.adempiere.model.InterfaceWrapperHelper.newInstance;
import static org.adempiere.model.InterfaceWrapperHelper.saveRecord;

import org.adempiere.user.api.IUserBL;
import org.compiere.model.I_AD_User;
import org.springframework.stereotype.Repository;

import de.metas.bpartner.BPartnerId;
import de.metas.bpartner.service.IBPartnerBL;
import de.metas.i18n.Language;
import de.metas.util.Services;
import lombok.NonNull;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
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

@Repository
public class UserRepository
{
	public User getById(@NonNull final UserId userId)
	{
		final I_AD_User userRecord = loadOutOfTrx(userId.getRepoId(), I_AD_User.class);
		return ofRecord(userRecord);
	}

	public User ofRecord(@NonNull final I_AD_User userRecord)
	{
		final IUserBL userBL = Services.get(IUserBL.class);
		final IBPartnerBL bPartnerBL = Services.get(IBPartnerBL.class);

		final Language userLanguage = Language.asLanguage(userRecord.getAD_Language());
		final Language bPartnerLanguage = bPartnerBL.getLanguageForModel(userRecord);
		final Language language = userBL.getUserLanguage(userRecord);

		return User.builder()
				.bpartnerId(BPartnerId.ofRepoIdOrNull(userRecord.getC_BPartner_ID()))
				.id(UserId.ofRepoId(userRecord.getAD_User_ID()))
				.name(userRecord.getName())
				.emailAddress(userRecord.getEMail())
				.userLanguage(userLanguage)
				.bPartnerLanguage(bPartnerLanguage)
				.language(language)
				.build();
	}

	public User save(@NonNull final User user)
	{
		final I_AD_User userRecord;
		if (user.getId() == null)
		{
			userRecord = newInstance(I_AD_User.class);
		}
		else
		{
			userRecord = load(user.getId().getRepoId(), I_AD_User.class);
		}

		userRecord.setName(user.getName());
		userRecord.setEMail(user.getEmailAddress());
		userRecord.setAD_Language(Language.asLanguageString(user.getLanguage()));
		saveRecord(userRecord);

		return user
				.toBuilder()
				.id(UserId.ofRepoId(userRecord.getAD_User_ID()))
				.build();
	}
}
