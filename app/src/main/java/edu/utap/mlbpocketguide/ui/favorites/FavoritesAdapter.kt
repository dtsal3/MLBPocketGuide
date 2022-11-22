package edu.utap.mlbpocketguide.ui.favorites

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.databinding.RvFavPlayerRowBinding


class FavoritesAdapter(private val viewModel: FavoritesViewModel)
    : ListAdapter<PlayerInfo, FavoritesAdapter.VH>(PlayerDiff()) {
        companion object {
            val firstNameKey = "firstName"
            val lastNameKey = "lastName"
        }
    class PlayerDiff : DiffUtil.ItemCallback<PlayerInfo>() {
        override fun areItemsTheSame(oldItem: PlayerInfo, newItem: PlayerInfo): Boolean {
            return oldItem.lastName == newItem.lastName && oldItem.firstName == newItem.firstName
        }
        override fun areContentsTheSame(oldItem: PlayerInfo, newItem: PlayerInfo): Boolean {
            return oldItem.firstName == newItem.firstName &&
                    oldItem.lastName == newItem.lastName &&
                    oldItem.birth == newItem.birth &&
                    oldItem.team == newItem.team &&
                    oldItem.lg == newItem.lg &&
                    oldItem.pos == newItem.pos &&
                    oldItem.fanGraphsID == newItem.fanGraphsID &&
                    oldItem.bats == newItem.bats &&
                    oldItem.throws == newItem.throws
        }
    }

    inner class VH(val binding: RvFavPlayerRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(player: PlayerInfo) {
            Log.d("Tracing", "In bind within the adapter VH class")
            binding.playerName.text = player.firstName + " " + player.lastName
            val team = player.team.lowercase()
            Log.d("Tracing", "trying to find the logo for %s".format(team))
            val context: Context = binding.teamLogo.context
            val logoId = context.resources.getIdentifier(team, "drawable", context.packageName)
            binding.teamLogo.background = context.getDrawable(logoId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.d("Tracing", "We are in onCreateViewHolder of the favorites adapter")
        val binding = RvFavPlayerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
        // open the onePost view if we tap on the title
        holder.binding.removeButton.setOnClickListener {
           Log.d("FavoritesAdapter", "If I had code to remove a favorite from the adapter, it'd be here")
            viewModel.removeFavorite(holder.binding.playerName.text.toString())
        }
    }
}

