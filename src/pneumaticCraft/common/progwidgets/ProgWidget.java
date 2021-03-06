package pneumaticCraft.common.progwidgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.common.entity.living.EntityDrone;
import pneumaticCraft.lib.Log;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ProgWidget implements IProgWidget{
    private int x, y;
    private IProgWidget[] connectedParameters;
    private IProgWidget outputStepConnection;
    private IProgWidget parent;

    // private static Gui gui;
    public ProgWidget(){
        if(getParameters() != null) connectedParameters = new IProgWidget[getParameters().length * 2]; //times two because black- and whitelist.
    }

    @Override
    public void getTooltip(List<String> curTooltip){
        curTooltip.add(EnumChatFormatting.DARK_AQUA + I18n.format("programmingPuzzle." + getWidgetString() + ".name"));
        if(getOptionWindow(null) != null) {
            curTooltip.add(EnumChatFormatting.GOLD + "Right click for options");
        }
    }

    @Override
    public int getX(){
        return x;
    }

    @Override
    public int getY(){
        return y;
    }

    @Override
    public void setX(int x){
        this.x = x;
    }

    @Override
    public void setY(int y){
        this.y = y;
    }

    @Override
    public int getWidth(){
        return 30;
    }

    @Override
    public int getHeight(){
        return 22;
    }

    @Override
    public void setParent(IProgWidget widget){
        parent = widget;
    }

    @Override
    public IProgWidget getParent(){
        return parent;
    }

    @Override
    public void render(){
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(getTexture());
        int width = getWidth() + (getParameters() != null && getParameters().length > 0 ? 10 : 0);
        int height = getHeight() + (hasStepOutput() ? 10 : 0);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(0, 0, 0, 0, 0);
        t.addVertexWithUV(0, height, 0, 0, 1);
        t.addVertexWithUV(width, height, 0, 1, 1);
        t.addVertexWithUV(width, 0, 0, 1, 0);
        t.draw();
    }

    protected abstract ResourceLocation getTexture();

    @Override
    public boolean hasStepOutput(){
        return hasStepInput();
    }

    @Override
    public EntityAIBase getWidgetTargetAI(EntityDrone drone, IProgWidget widget){
        return null;
    }

    @Override
    public EntityAIBase getWidgetAI(EntityDrone drone, IProgWidget widget){
        return null;
    }

    @Override
    public void setParameter(int index, IProgWidget parm){
        if(connectedParameters != null) connectedParameters[index] = parm;
    }

    @Override
    public IProgWidget[] getConnectedParameters(){
        return connectedParameters;
    }

    @Override
    public void setOutputWidget(IProgWidget widget){
        outputStepConnection = widget;
    }

    @Override
    public IProgWidget getOutputWidget(){
        return outputStepConnection;
    }

    @Override
    public IProgWidget copy(){
        try {
            IProgWidget copy = this.getClass().newInstance();
            NBTTagCompound tag = new NBTTagCompound();
            writeToNBT(tag);
            copy.readFromNBT(tag);
            return copy;
        } catch(Exception e) {
            Log.error("Error occured when trying to copy an " + getWidgetString() + " widget.");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        tag.setString("name", getWidgetString());
        tag.setInteger("x", x);
        tag.setInteger("y", y);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        x = tag.getInteger("x");
        y = tag.getInteger("y");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getOptionWindow(GuiProgrammer guiProgrammer){
        return null;
    }

    public static List getConnectedWidgetList(IProgWidget widget, int parameterIndex){
        IProgWidget connectingWidget = widget.getConnectedParameters()[parameterIndex];
        if(connectingWidget != null) {
            List list = new ArrayList();
            while(connectingWidget != null) {
                list.add(connectingWidget);
                connectingWidget = connectingWidget.getConnectedParameters()[0];
            }
            return list;
        } else {
            return null;
        }
    }

}
