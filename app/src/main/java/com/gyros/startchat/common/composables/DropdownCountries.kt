package com.gyros.startchat.common.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gyros.startchat.R
import com.gyros.startchat.data.models.CountryCode

@Composable
fun DropdownCountries(
    modifier: Modifier = Modifier,
    countryCodeSelected: CountryCode?,
    countryCodes: List<CountryCode>?,
    onCountryCodeSelected: (CountryCode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = countryCodeSelected?.let {
                    "${it.dialCode} ${it.flag}"
                } ?: "",
                style = TextStyle(
                    fontSize = 18.sp,
                    platformStyle = PlatformTextStyle()
                )
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = countryCodeSelected?.name ?: stringResource(R.string.start_chat_select_country_code),
                color = Color.Gray,
                style = TextStyle(
                    fontSize = 18.sp,
                    platformStyle = PlatformTextStyle(),
                    fontFamily = FontFamily.Default
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.start_chat_dropdown_icon),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            countryCodes?.forEach { countryCode ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Text(
                                modifier = Modifier.defaultMinSize(minWidth = 60.dp),
                                text = countryCode.dialCode,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    platformStyle = PlatformTextStyle(),
                                    fontFamily = FontFamily.Default
                                ),
                            )
                            Text(
                                text = "${countryCode.flag} ${countryCode.name}",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    platformStyle = PlatformTextStyle(),
                                    fontFamily = FontFamily.Default
                                ),
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onCountryCodeSelected(countryCode)
                    }
                )
            }
        }
    }
}