// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.pitchbend;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendCommand extends AbstractPitchbendCommand<PushControlSurface, PushConfiguration>
{
    private int pitchValue = 0;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PitchbendCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int data1, final int data2)
    {
        // Don't get in the way of configuration
        if (this.surface.isShiftPressed ())
            return;

        final PushConfiguration config = this.surface.getConfiguration ();
        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_PITCH:
                this.surface.sendMidiEvent (0xE0, data1, data2);
                break;

            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), data2);
                this.pitchValue = data2;
                break;

            case PushConfiguration.RIBBON_MODE_CC_PB:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 127 - data2 * 2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_PB_CC:
                if (data2 > 64)
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), (data2 - 64) * 2);
                else if (data2 < 64)
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                else
                {
                    this.surface.sendMidiEvent (0xE0, data1, data2);
                    this.surface.sendMidiEvent (0xB0, config.getRibbonModeCCVal (), 0);
                }
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData selTrack = tb.getSelectedTrack ();
                if (selTrack != null)
                    tb.setVolume (selTrack.getIndex (), this.model.getValueChanger ().toDAWValue (data2));
                return;
        }

        this.surface.getOutput ().sendPitchbend (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void updateValue ()
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        switch (config.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_CC:
                this.surface.setRibbonValue (this.pitchValue);
                break;

            case PushConfiguration.RIBBON_MODE_FADER:
                final TrackData t = this.model.getCurrentTrackBank ().getSelectedTrack ();
                this.surface.setRibbonValue (t == null ? 0 : this.model.getValueChanger ().toMidiValue (config.isEnableVUMeters () ? t.getVu () : t.getVolume ()));
                break;

            default:
                this.surface.setRibbonValue (64);
                break;
        }
    }
}
