package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityKrakenSlime;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;

public class ItemSerpentStaff extends ItemBase {

	public ItemSerpentStaff() {
		super(Names.Items.SERPENT_STAFF);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(200);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.BLOCK;
	}

	@Override
	public void onUsingTick(ItemStack serpentStaff, EntityLivingBase entity, int count) {
		if(entity.world.isRemote || !(entity instanceof EntityPlayer) || count % 3 != 0)
			return;

		shootKrakenSlime(serpentStaff, (EntityPlayer) entity);
	}

	private void shootKrakenSlime(ItemStack serpentStaff, EntityPlayer player) {
		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		EntityKrakenSlime krakenSlime = new EntityKrakenSlime(player.world, player);
		krakenSlime.shoot(player, player.rotationPitch, player.rotationYaw, 0F, 1.5F, 1.0F);
		player.world.spawnEntity(krakenSlime);
		serpentStaff.damageItem(1, player);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack serpentStaff, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

		if (!entityLiving.world.isRemote && timeLeft + 2 >= serpentStaff.getMaxItemUseDuration() && entityLiving instanceof EntityPlayer)
			shootKrakenSlime(serpentStaff, (EntityPlayer) entityLiving);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		//drain effect
		int drain = player.world.rand.nextInt(4);
		if(entity.attackEntityFrom(DamageSource.causePlayerDamage(player), drain)) {
			player.heal(drain);
			stack.damageItem(1, player);
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 11;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

}
