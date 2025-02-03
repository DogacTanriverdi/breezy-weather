/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.ui.main.adapters.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import org.breezyweather.R
import org.breezyweather.common.extensions.DEFAULT_CARD_LIST_ITEM_ELEVATION_DP
import org.breezyweather.common.extensions.isDarkMode
import org.breezyweather.databinding.ItemLocationCardBinding
import org.breezyweather.domain.location.model.isDaylight
import org.breezyweather.ui.main.utils.MainThemeColorProvider
import org.breezyweather.ui.theme.resource.providers.ResourceProvider

class LocationHolder(
    private val mBinding: ItemLocationCardBinding,
    private val mClickListener: (String) -> Unit,
    private val mDragListener: (LocationHolder) -> Unit,
) : RecyclerView.ViewHolder(mBinding.root) {
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun onBindView(context: Context, model: LocationModel, resourceProvider: ResourceProvider) {
        val lightTheme = !context.isDarkMode
        val elevatedSurfaceColor = org.breezyweather.common.utils.ColorUtils.getWidgetSurfaceColor(
            DEFAULT_CARD_LIST_ITEM_ELEVATION_DP,
            MainThemeColorProvider.getColor(lightTheme, androidx.appcompat.R.attr.colorPrimary),
            MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorSurface)
        )
        val talkBackBuilder = StringBuilder()
        if (model.currentPosition) {
            talkBackBuilder.append(context.getString(R.string.location_current))
        }
        if (talkBackBuilder.toString().isNotEmpty()) {
            talkBackBuilder.append(context.getString(R.string.comma_separator))
        }
        mBinding.container.apply {
            swipe(0f)
            iconResStart = R.drawable.ic_delete
        }
        mBinding.container.iconResEnd = R.drawable.ic_settings
        mBinding.container.apply {
            backgroundColorStart =
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorErrorContainer)
            backgroundColorEnd = if (model.location.isCurrentPosition) {
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorTertiaryContainer)
            } else {
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorSecondaryContainer)
            }
            tintColorStart =
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorOnErrorContainer)
            tintColorEnd = if (model.location.isCurrentPosition) {
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorOnTertiaryContainer)
            } else {
                MainThemeColorProvider.getColor(
                    lightTheme,
                    com.google.android.material.R.attr.colorOnSecondaryContainer
                )
            }
        }
        mBinding.item.setBackgroundColor(
            if (model.selected) {
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorPrimaryContainer)
            } else {
                elevatedSurfaceColor
            }
        )
        ImageViewCompat.setImageTintList(
            mBinding.sortButton,
            ColorStateList.valueOf(
                if (model.selected) {
                    MainThemeColorProvider.getColor(
                        lightTheme,
                        com.google.android.material.R.attr.colorOnPrimaryContainer
                    )
                } else {
                    MainThemeColorProvider.getColor(lightTheme, androidx.appcompat.R.attr.colorPrimary)
                }
            )
        )
        mBinding.sortButton.visibility = View.VISIBLE
        mBinding.content.setPaddingRelative(0, 0, 0, 0)
        if (model.weatherCode != null) {
            mBinding.weatherIcon.apply {
                visibility = View.VISIBLE
                setImageDrawable(resourceProvider.getWeatherIcon(model.weatherCode, model.location.isDaylight))
            }
        } else {
            mBinding.weatherIcon.visibility = View.GONE
        }
        mBinding.title1.setTextColor(
            if (model.selected) {
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorOnPrimaryContainer)
            } else {
                MainThemeColorProvider.getColor(lightTheme, R.attr.colorTitleText)
            }
        )
        mBinding.title1.text = model.title
        if (model.body.isEmpty()) {
            mBinding.title2.visibility = View.GONE
        } else {
            mBinding.title2.visibility = View.VISIBLE
            mBinding.title2.setTextColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorBodyText))
            mBinding.title2.text = model.body
        }

        mBinding.container.setOnClickListener { mClickListener(model.location.formattedId) }
        // TODO
        mBinding.sortButton.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mDragListener(this)
            }
            false
        }
        talkBackBuilder.append(context.getString(R.string.comma_separator))
            .append(context.getString(R.string.location_swipe_to_delete))
        itemView.contentDescription = talkBackBuilder.toString()
    }
}
