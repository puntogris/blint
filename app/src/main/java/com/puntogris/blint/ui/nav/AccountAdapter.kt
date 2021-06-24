/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.puntogris.blint.ui.nav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.databinding.AccountItemLayoutBinding
import com.puntogris.blint.diffcallback.BusinessDiffCallback
import com.puntogris.blint.diffcallback.BusinessItemDiffCallBack
import com.puntogris.blint.model.BusinessItem
import com.puntogris.blint.model.Employee

/**
 * An adapter which holds a list of selectable accounts owned by the current user.
 */
class AccountAdapter(
    private val listener: AccountAdapterListener
) : ListAdapter<BusinessItem, AccountViewHolder>(BusinessItemDiffCallBack()) {

    interface AccountAdapterListener {
        fun onAccountClicked(employee: BusinessItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            AccountItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}