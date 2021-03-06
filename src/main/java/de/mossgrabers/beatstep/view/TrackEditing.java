// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;


/**
 * Pluggable extension to edit track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackEditing
{
    private BeatstepControlSurface surface;
    private Model                  model;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackEditing (final BeatstepControlSurface surface, final Model model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * A knob is moved for changing a track parameter.
     *
     * @param index The index of the knob
     * @param value The knobs value
     */
    public void onTrackKnob (final int index, final int value)
    {
        if (value == 64)
            return;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                tb.changeVolume (selectedTrack.getIndex (), value);
                break;
            case 1:
                tb.changePan (selectedTrack.getIndex (), value);
                break;

            case 2:
                tb.setMute (selectedTrack.getIndex (), value > 64);
                break;

            case 3:
                tb.setSolo (selectedTrack.getIndex (), value > 64);
                break;

            case 4:
                tb.changeCrossfadeModeAsNumber (selectedTrack.getIndex (), value);
                break;

            case 5:
                this.model.getTransport ().changeTempo (value >= 65);
                break;

            case 6:
                this.model.getTransport ().changePosition (value >= 65, this.surface.isShiftPressed ());
                break;

            case 7:
                this.model.getMasterTrack ().changeVolume (value);
                break;

            // Send 1 - 4
            case 8:
            case 9:
            case 10:
            case 11:
                if (tb instanceof TrackBankProxy)
                    ((TrackBankProxy) tb).changeSend (selectedTrack.getIndex (), index - 8, value);
                break;
        }
    }
}