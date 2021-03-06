// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to handle the shift button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ShiftCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final boolean isUp = event == ButtonEvent.UP;
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SHIFT, isUp ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_HI);

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer cm = modeManager.getActiveModeId ();
        if (event == ButtonEvent.DOWN && cm == Modes.MODE_SCALES)
            modeManager.setActiveMode (Modes.MODE_SCALE_LAYOUT);
        else if (isUp && cm == Modes.MODE_SCALE_LAYOUT)
            modeManager.restoreMode ();

        this.model.getValueChanger ().setSpeed (this.surface.isShiftPressed ());
    }
}
