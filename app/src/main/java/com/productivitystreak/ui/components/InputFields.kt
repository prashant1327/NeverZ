package com.productivitystreak.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.productivitystreak.ui.theme.*

/**
 * Material 3 Input Field Components
 * Consistent text input components with proper theming
 */

/**
 * Styled Text Field - Outlined text field with M3 theming
 * Best for general text input
 */
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = Shapes.small,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = if (trailingIcon != null && onTrailingIconClick != null) {
            {
                IconButton(onClick = onTrailingIconClick) {
                    Icon(trailingIcon, contentDescription = null)
                }
            }
        } else trailingIcon?.let { { Icon(it, contentDescription = null) } },
        supportingText = (supportingText ?: errorText)?.let { { Text(it) } },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

/**
 * Multiline Text Field - Text area with auto-resize
 * Best for longer text input like notes or comments
 */
@Composable
fun MultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    minLines: Int = 3,
    maxLines: Int = 8,
    maxCharacters: Int? = null,
    shape: Shape = Shapes.small
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (maxCharacters == null || it.length <= maxCharacters) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            supportingText = supportingText?.let { { Text(it) } },
            isError = isError,
            minLines = minLines,
            maxLines = maxLines,
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors()
        )
        
        if (maxCharacters != null) {
            Text(
                text = "${value.length}/$maxCharacters",
                style = MaterialTheme.typography.bodySmall,
                color = if (value.length >= maxCharacters) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xxs, end = Spacing.md)
            )
        }
    }
}

/**
 * Search Field - Search input with leading icon and clear button
 * Best for search functionality
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = "Search...",
    onClear: () -> Unit = { onValueChange("") },
    onSearch: (() -> Unit)? = null,
    shape: Shape = Shapes.full
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(
            imeAction = if (onSearch != null) ImeAction.Search else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch?.invoke() }
        ),
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    )
}

/**
 * Number Field - Numeric input with increment/decrement buttons
 * Best for numeric input with constraints
 */
@Composable
fun NumberField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    shape: Shape = Shapes.small
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    
    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            newValue.toIntOrNull()?.let { intValue ->
                if (intValue in min..max) {
                    onValueChange(intValue)
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = label?.let { { Text(it) } },
        leadingIcon = {
            IconButton(
                onClick = {
                    val newValue = (value - step).coerceAtLeast(min)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                enabled = enabled && value > min
            ) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    val newValue = (value + step).coerceAtMost(max)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                enabled = enabled && value < max
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors()
    )
}
