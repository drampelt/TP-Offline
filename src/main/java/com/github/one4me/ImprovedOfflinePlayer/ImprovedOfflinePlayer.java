//Copyright (C) 2012 one4me@github.com
//ImprovedOfflinePlayer Version 1.0.0
package com.github.one4me.ImprovedOfflinePlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

public class ImprovedOfflinePlayer {
  private String player;
  private File file;
  private NBTTagCompound compound;
  private boolean exists = true;
  private String pluginName;
  public ImprovedOfflinePlayer(String name) {
    this.player = name;
    loadOfflinePlayer();
  }
  private void loadOfflinePlayer() {
    try {
      for(World w : Bukkit.getWorlds()) {
        File check = new File(w.getWorldFolder(),"players" + File.separator + this.player + ".dat");
        if(check.exists()){
          this.exists = true;
          this.file = check;
          this.compound = NBTCompressedStreamTools.a(new FileInputStream(this.file));
          this.pluginName = ImprovedOfflinePlayer.class.getProtectionDomain().getCodeSource().toString();
          this.pluginName = this.pluginName.substring(this.pluginName.lastIndexOf("/") + 1, this.pluginName.lastIndexOf("."));
          return;
        }
        this.exists = false;
      }
    }
    catch(Exception e) {
      this.exists = false;
    }
  }
  private void saveOfflineData() {
    if(this.exists) {
      try {
        NBTCompressedStreamTools.a(this.compound, new FileOutputStream(this.file));
      }
      catch(Exception e) {
        this.exists = false;
      }
    }
  }
  public boolean exists() {
    return this.exists;
  }
  public String getName() {
    return player;
  }
  public String[] getFileString() {
	NBTTagList list = this.compound.getCompound("data").getCompound(this.pluginName).getList("Messages");
    String[] message = new String[list.size()];
    for(int i = 0; i < list.size(); i++) {
      message[i] = list.get(i).toString();
    }
    return message;
  }
  public void setFileString(String message) {
    if(!this.compound.hasKey("data")) {
      this.compound.setCompound("data", new NBTTagCompound());
    }
    if(!this.compound.getCompound("data").hasKey(this.pluginName)) {
      this.compound.getCompound("data").setCompound(this.pluginName, new NBTTagCompound());
    }
    if(!this.compound.getCompound("data").getCompound(this.pluginName).hasKey("Messages")) {
      this.compound.getCompound("data").getCompound(this.pluginName).set("Messages", new NBTTagList());
    }
    this.compound.getCompound("data").getCompound(this.pluginName).getList("Messages").add(new NBTTagString(null, message));
    saveOfflineData();
  }
  public float getFoodExhaustionLevel() {
    return this.compound.getFloat("foodExhaustionLevel");
  }
  public void setFoodExhaustionLevel(float input) {
    this.compound.setFloat("foodExhaustionLevel", input);
    saveOfflineData();
  }
  public int getFoodLevel() {
    return this.compound.getInt("foodLevel");
  }
  public void setFoodLevel(int input) {
    this.compound.setInt("foodLevel", input);
    saveOfflineData();
  }
  public float getFoodSaturationLevel() {
    return this.compound.getFloat("foodSaturationLevel");
  }
  public void setFoodSaturationLevel(float input) {
    this.compound.setFloat("foodSaturationLevel", input);
    saveOfflineData();
  }
  public int getFoodTickTimer() {
    return this.compound.getInt("foodTickTimer");
  }
  public void setFoodTickTimer(int input) {
    this.compound.setInt("foodTickTimer", input);
    saveOfflineData();
  }
  public ItemStack[] getInventoryArmor() {
    ItemStack[] armor = new ItemStack[4];
    NBTTagList list = this.compound.getList("Inventory");
    for (int i = 0; i < list.size(); i++) {
      NBTTagCompound item = (NBTTagCompound)list.get(i);
      byte slot = item.getByte("Slot");
      short id = item.getShort("id");
      byte count = item.getByte("Count");
      short damage = item.getShort("Damage");
      if (slot >= 100) {
        armor[(slot - 100)] = new ItemStack(id, count, damage);
        if(item.hasKey("tag")) {
          Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
          NBTTagCompound tCompound = item.getCompound("tag");
          if(tCompound.hasKey("ench")) {
            NBTTagList enchTL = tCompound.getList("ench");
            for(int f = 0; f < enchTL.size(); f++) {
              NBTTagCompound enchTC = (NBTTagCompound)tCompound.getList("ench").get(f);
              short idTC = enchTC.getShort("id");
              short lvlTC = enchTC.getShort("lvl");
              enchantments.put(Enchantment.getById((int)idTC), (int)lvlTC);
            }
            armor[(slot - 100)].addUnsafeEnchantments(enchantments);
          }
        }
      }
    }
	return armor;
  }
  public void setInventoryArmor(ItemStack[] armor) {
    for(int i = 0; i < 4; i++) {
      if(armor[i] != null) {
        NBTTagCompound iCompound = new NBTTagCompound();
        iCompound.setByte("Slot", (byte)(i));
        iCompound.setShort("id", (short)armor[i].getType().getId());
        iCompound.setByte("Count", (byte)armor[i].getAmount());
        iCompound.setShort("Damage", (short)armor[i].getDurability());
        if(!armor[i].getEnchantments().isEmpty()) {
          NBTTagCompound tag = new NBTTagCompound();
          Map<Enchantment, Integer> mapEnchantments = armor[i].getEnchantments();
          for (Map.Entry<Enchantment, Integer> entry : mapEnchantments.entrySet()) {
            NBTTagCompound enchantments = new NBTTagCompound();
            enchantments.setShort("id", (short)entry.getKey().getId());
            enchantments.setShort("lvl", entry.getValue().shortValue());
            tag.getList("ench").add(enchantments);
          }
          iCompound.setCompound("tag", tag);
        }
        this.compound.getList("Inventory").add(iCompound);
      }
    }
  }
  public ItemStack[] getInventoryEnd() {
    ItemStack[] inventory = new ItemStack[27];
    NBTTagList list = this.compound.getList("EnderItems");
    for (int i = 0; i < list.size(); i++) {
      NBTTagCompound item = (NBTTagCompound)list.get(i);
      byte slot = item.getByte("Slot");
      short id = item.getShort("id");
      byte count = item.getByte("Count");
      short damage = item.getShort("Damage");
      inventory[slot] = new ItemStack(id, count, damage);
      if(item.hasKey("tag")) {
        Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        NBTTagCompound tCompound = item.getCompound("tag");
        if(tCompound.hasKey("ench")) {
          NBTTagList enchTL = tCompound.getList("ench");
          for(int f = 0; f < enchTL.size(); f++) {
            NBTTagCompound enchTC = (NBTTagCompound)tCompound.getList("ench").get(f);
            short idTC = enchTC.getShort("id");
            short lvlTC = enchTC.getShort("lvl");
            enchantments.put(Enchantment.getById((int)idTC), (int)lvlTC);
          }
          inventory[slot].addUnsafeEnchantments(enchantments);
        }
      }
    }
    return inventory;
  }
  public void setInventoryEnd(ItemStack[] items) {
    for(int i = 0; i < 27; i++) {
      if(items[i] != null) {
        NBTTagCompound iCompound = new NBTTagCompound();
        iCompound.setByte("Slot", (byte)(i));
        iCompound.setShort("id", (short)items[i].getType().getId());
        iCompound.setByte("Count", (byte)items[i].getAmount());
        iCompound.setShort("Damage", (short)items[i].getDurability());
        if(!items[i].getEnchantments().isEmpty()) {
          NBTTagCompound tag = new NBTTagCompound();
          tag.set("ench", new NBTTagList());
          Map<Enchantment, Integer> mapEnchantments = items[i].getEnchantments();
          for (Map.Entry<Enchantment, Integer> entry : mapEnchantments.entrySet()) {
            NBTTagCompound enchantments = new NBTTagCompound();
            enchantments.setShort("id", (short)entry.getKey().getId());
            enchantments.setShort("lvl", entry.getValue().shortValue());
            tag.getList("ench").add(enchantments);
          }
          iCompound.setCompound("tag", tag);
        }
        this.compound.getList("Inventory").add(iCompound);
      }
    }
    saveOfflineData();
  }
  public ItemStack[] getInventoryItems() {
    ItemStack[] items = new ItemStack[36];
    NBTTagList list = this.compound.getList("Inventory");
    for (int i = 0; i < list.size(); i++) {
      NBTTagCompound item = (NBTTagCompound)list.get(i);
      byte slot = item.getByte("Slot");
      short id = item.getShort("id");
      byte count = item.getByte("Count");
      short damage = item.getShort("Damage");
      if (slot < 100) {
        items[slot] = new ItemStack(id, count, damage);
        if(item.hasKey("tag")) {
          Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
          NBTTagCompound tCompound = item.getCompound("tag");
          if(tCompound.hasKey("ench")) {
            NBTTagList enchTL = tCompound.getList("ench");
            for(int f = 0; f < enchTL.size(); f++) {
              NBTTagCompound enchTC = (NBTTagCompound)tCompound.getList("ench").get(f);
              short idTC = enchTC.getShort("id");
              short lvlTC = enchTC.getShort("lvl");
              enchantments.put(Enchantment.getById((int)idTC), (int)lvlTC);
            }
            items[slot].addUnsafeEnchantments(enchantments);
          }
        }
      }
    }
	return items;
  }
  public void setInventoryItems(ItemStack[] items) {
    for(int i = 0; i < 36; i++) {
      if(items[i] != null) {
        NBTTagCompound iCompound = new NBTTagCompound();
        iCompound.setByte("Slot", (byte)(i));
        iCompound.setShort("id", (short)items[i].getType().getId());
        iCompound.setByte("Count", (byte)items[i].getAmount());
        iCompound.setShort("Damage", (short)items[i].getDurability());
        if(!items[i].getEnchantments().isEmpty()) {
          NBTTagCompound tag = new NBTTagCompound();
          tag.set("ench", new NBTTagList());
          Map<Enchantment, Integer> mapEnchantments = items[i].getEnchantments();
          for (Map.Entry<Enchantment, Integer> entry : mapEnchantments.entrySet()) {
            NBTTagCompound enchantments = new NBTTagCompound();
            enchantments.setShort("id", (short)entry.getKey().getId());
            enchantments.setShort("lvl", entry.getValue().shortValue());
            tag.getList("ench").add(enchantments);
          }
          iCompound.setCompound("tag", tag);
        }
        this.compound.getList("Inventory").add(iCompound);
      }
    }
  }
  public boolean getIsGrounded() {
    return compound.getBoolean("OnGround");
  }
  public void setInGrounded(boolean input) {
    this.compound.setBoolean("OnGround", input);
    saveOfflineData();
  }
  public boolean getIsSleeping() {
    return this.compound.getBoolean("Sleeping");
  }
  public void setIsSleeping(boolean input) {
    this.compound.setBoolean("Sleeping", input);
    saveOfflineData();
  }
  public float getMiscFallDistance() {
    return this.compound.getFloat("FallDistance");
  }
  public void setMiscFallDistance(float input) {
    this.compound.setFloat("FallDistance", input);
    saveOfflineData();
  }
  public int getMiscGameMode() {
    return this.compound.getInt("playerGameType");
  }
  public void setMiscGameMode(int input) {
    this.compound.setInt("playerGameType", input);
    saveOfflineData();
  }
  public short getMiscHealth() {
    return this.compound.getShort("Health");
  }
  public void setMiscHealth(short input) {
    this.compound.setShort("Health", input);
    saveOfflineData();
  }
  public Location getMiscLocation() {
    World w = getLocationWorld();
    double[] p = getLocationPosition();
    float[] r = getLocationView();
    Location l = new Location(w, p[0], p[1], p[2], r[0], r[1]);
    return l;
  }
  public void setMiscLocation(Location location) {
    setLocationWorld(location.getWorld().getUID().getLeastSignificantBits(), location.getWorld().getUID().getMostSignificantBits());
    setDimension(location.getWorld().getEnvironment().getId());
    setLocationPosition(location.getX(), location.getY(), location.getZ());
    setLocationView(location.getYaw(), location.getPitch());
    saveOfflineData();
  }
  public double[] getMiscVelocity() {
    double[] velocity = new double[3];
    NBTTagList list = this.compound.getList("Motion");
    velocity[0] = ((NBTTagDouble)list.get(0)).data;
    velocity[1] = ((NBTTagDouble)list.get(1)).data;
    velocity[2] = ((NBTTagDouble)list.get(2)).data;
    return velocity;
  }
  public void setMiscVelocity(double x, double y, double z) {
    this.compound.set("Motion", new NBTTagList());
    this.compound.getList("Motion").add(new NBTTagDouble("", x));
    this.compound.getList("Motion").add(new NBTTagDouble("", y));
    this.compound.getList("Motion").add(new NBTTagDouble("", z));
    saveOfflineData();
  }
  public ArrayList<PotionEffect> getPotionEffects() {
    ArrayList<PotionEffect> abilities = new ArrayList<PotionEffect>();
    if(this.compound.hasKey("ActiveEffects")) {
      NBTTagList list = this.compound.getList("ActiveEffects");
      for (int i = 0; i < list.size(); i++) {
        NBTTagCompound effect = (NBTTagCompound)list.get(i);
        byte amp = effect.getByte("Amplifier");
        byte id = effect.getByte("Id");
        int time = effect.getInt("Duration");
        abilities.add(new PotionEffect(PotionEffectType.getById(id), time, amp));
      }
    }
    return abilities;
  }
  public void setPotionEffects(Collection<PotionEffect> effects) {
    this.compound.set("ActiveEffects", new NBTTagList());
    for(PotionEffect pe : effects) {
      NBTTagCompound eCompound = new NBTTagCompound();
      eCompound.setByte("Amplifier", (byte)(pe.getAmplifier()));
      eCompound.setByte("Id", (byte)(pe.getType().getId()));
      eCompound.setInt("Duration", (int)(pe.getDuration()));
      this.compound.getList("ActiveEffects").add(eCompound);
      System.out.println("test");
    }
    if(!(this.compound.getList("ActiveEffects").size() > 0)) {
      this.compound.remove("ActiveEffects");
    }
    saveOfflineData();
  }
  public short getTicksAir() {
    return this.compound.getShort("Air");
  }
  public void setTicksAir(short input) {
    this.compound.setShort("Air", input);
    saveOfflineData();
  }
  public short getTicksFire() {
    return this.compound.getShort("Fire");
  }
  public void setTicksFire(short input) {
    this.compound.setShort("Fire", input);
    saveOfflineData();
  }
  public short getTimeAttack() {
    return this.compound.getShort("AttackTime");
  }
  public void setTimeAttack(short input) {
    this.compound.setShort("AttackTime", input);
    saveOfflineData();
  }
  public short getTimeDeath() {
    return this.compound.getShort("DeathTime");
  }
  public void setTimeDeath(short input) {
    this.compound.setShort("DeathTime", input);
    saveOfflineData();
  }
  public short getTimeHurt() {
    return this.compound.getShort("HurtTime");
  }
  public void setTimeHurt(short input) {
    this.compound.setShort("HurtTime", input);
    saveOfflineData();
  }
  public short getTimeSleep() {
    return this.compound.getShort("SleepTime");
  }
  public void setTimeSleep(short input) {
    this.compound.setShort("SleepTime", input);
    saveOfflineData();
  }
  public int getXpLevel() {
    return this.compound.getInt("XpLevel");
  }
  public void setXpLevel(int input) {
    this.compound.setInt("XpLevel", input);
    saveOfflineData();
  }
  public float getXpProgress() {
    return this.compound.getFloat("XpP");
  }
  public void setXpProgress(float input) {
    this.compound.setFloat("XpP", input);
    saveOfflineData();
  }
  public int getXpTotal() {
    return this.compound.getInt("XpTotal");
  }
  public void setXpTotal(int input) {
    this.compound.setInt("XpTotal", input);
    saveOfflineData();
  }
  private void setDimension(int input) {
    this.compound.setInt("Dimension", input);
    saveOfflineData();
  }
  private float[] getLocationView() {
    float[] rotation = new float[2];
    NBTTagList list = this.compound.getList("Rotation");
    rotation[0] = ((NBTTagFloat)list.get(0)).data;
    rotation[1] = ((NBTTagFloat)list.get(1)).data;
    return rotation;
  }
  private void setLocationView(float yaw, float pitch) {
    this.compound.set("Rotation", new NBTTagList());
    this.compound.getList("Rotation").add(new NBTTagFloat("", yaw));
    this.compound.getList("Rotation").add(new NBTTagFloat("", pitch));
    saveOfflineData();
  }
  private double[] getLocationPosition() {
    double[] pos = new double[3];
    NBTTagList list = this.compound.getList("Pos");
    pos[0] = ((NBTTagDouble)list.get(0)).data;
    pos[1] = ((NBTTagDouble)list.get(1)).data;
    pos[2] = ((NBTTagDouble)list.get(2)).data;
    return pos;
  }
  private void setLocationPosition(double x, double y, double z) {
    this.compound.set("Pos", new NBTTagList());
    this.compound.getList("Pos").add(new NBTTagDouble("", x));
    this.compound.getList("Pos").add(new NBTTagDouble("", y));
    this.compound.getList("Pos").add(new NBTTagDouble("", z));
    saveOfflineData();
  }
  private World getLocationWorld() {
    return Bukkit.getWorld(new UUID(getUUIDWorldMost(), getUUIDWorldLeast()));
  }
  private void setLocationWorld(long l, long m) {
    setUUIDWorldLeast(l);
    setUUIDWorldMost(m);
  }
  private long getUUIDWorldLeast() {
    return this.compound.getLong("WorldUUIDLeast");
  }
  private void setUUIDWorldLeast(long input) {
    this.compound.setLong("WorldUUIDLeast", input);
    saveOfflineData();
  }
  private long getUUIDWorldMost() {
    return this.compound.getLong("WorldUUIDMost");
  }
  private void setUUIDWorldMost(long input) {
    this.compound.setLong("WorldUUIDMost", input);
    saveOfflineData();
  }
}