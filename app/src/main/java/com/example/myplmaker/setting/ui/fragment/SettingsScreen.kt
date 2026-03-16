package com.example.myplmaker.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myplmaker.R
import com.example.myplmaker.setting.ui.view.SettingViewModel

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun SettingsScreen(
    viewModel: SettingViewModel,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAgreementClick: () -> Unit
) {
    val isDarkTheme by viewModel.switchThemeLD.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_B22_FF))
    ) {
        Text(
            text = stringResource(R.string.nas),
            fontSize = 22.sp,
            fontFamily = YsDisplayMedium,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.black_white),
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.sixteen_dp),
                top = dimensionResource(R.dimen.sixteen_dp),
                bottom = dimensionResource(R.dimen.sixteen_dp)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.sixteen_dp),
                    vertical = dimensionResource(R.dimen.twenty_one_dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.black_tm),
                fontSize = 16.sp,
                fontFamily = YsDisplayRegular,
                color = colorResource(R.color.black_white),
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isDarkTheme?.darkTheme ?: false,
                onCheckedChange = { isChecked ->
                    viewModel.switchTheme(isChecked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(R.color.color_sw_b),
                    uncheckedThumbColor = colorResource(R.color.color_sw_w),
                    checkedTrackColor = colorResource(R.color.color_tr_b),
                    uncheckedTrackColor = colorResource(R.color.color_tr_w)
                )
            )
        }

        SettingsItem(
            text = stringResource(R.string.share),
            iconRes = R.drawable.comm,
            onClick = onShareClick
        )

        SettingsItem(
            text = stringResource(R.string.support),
            iconRes = R.drawable.tell,
            onClick = onSupportClick
        )

        SettingsItem(
            text = stringResource(R.string.agreement),
            iconRes = R.drawable.more,
            onClick = onAgreementClick
        )
    }
}

@Composable
private fun SettingsItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.sixteen_dp),
                vertical = dimensionResource(R.dimen.twenty_one_dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = YsDisplayRegular,
            color = colorResource(R.color.black_white),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colorResource(R.color.color_FF_B22),
            modifier = Modifier.size(dimensionResource(R.dimen.twenty_four_dp))
        )
    }
}
