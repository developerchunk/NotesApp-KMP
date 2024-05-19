package presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import domain.ToDoTask

@Composable
fun TaskView(
    task: ToDoTask,
    showActiveTask: Boolean,
    onSelect: (ToDoTask) -> Unit,
    onComplete: (ToDoTask, Boolean) -> Unit,
    onFavorite: (ToDoTask, Boolean) -> Unit,
    onDelete: (ToDoTask) -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            if (showActiveTask) onSelect(task)
            else onDelete(task)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    onComplete(task, !task.completed)
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.alpha(alpha = if (showActiveTask) 1f else 0.5f),
                text = task.title,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textDecoration = if (showActiveTask) TextDecoration.None else TextDecoration.LineThrough
            )

        }

        IconButton(
            onClick = {
                if (showActiveTask) onFavorite(task, !task.favorite)
                else onDelete(task)
            }
        ) {

            Icon(
                imageVector = if (showActiveTask) if (task.favorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder else Icons.Rounded.Delete,
                contentDescription = null,
                tint = if (task.favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

        }

    }

}