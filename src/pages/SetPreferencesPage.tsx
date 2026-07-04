import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { api } from '../services/api';
import './SetPreferencesPage.css';

export default function SetPreferencesPage() {
  const { meetingId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  const queryParams = new URLSearchParams(location.search);
  const initialEmail = queryParams.get('email') || '';

  const [email, setEmail] = useState(initialEmail);
  const [meeting, setMeeting] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState('');

  // Busy block state
  const [busyBlocks, setBusyBlocks] = useState([{ busyStart: '', busyEnd: '' }]);
  
  // Preference state
  const [preferences, setPreferences] = useState([{ preferenceType: 'PREFERRED_TIME_RANGE', value: '', weight: '5' }]);

  useEffect(() => {
    const fetchMeeting = async () => {
      if (!meetingId) return;
      setLoading(true);
      try {
        const response = await api.getMeeting(meetingId);
        setMeeting(response.data);
      } catch (err: any) {
        setError(err.message || 'Failed to load meeting details');
      } finally {
        setLoading(false);
      }
    };
    fetchMeeting();
  }, [meetingId]);

  const handleAddBusyBlock = () => setBusyBlocks([...busyBlocks, { busyStart: '', busyEnd: '' }]);
  const handleBusyBlockChange = (index: number, field: string, value: string) => {
    const newBlocks = [...busyBlocks];
    newBlocks[index] = { ...newBlocks[index], [field]: value };
    setBusyBlocks(newBlocks);
  };
  const handleRemoveBusyBlock = (index: number) => {
    const newBlocks = [...busyBlocks];
    newBlocks.splice(index, 1);
    setBusyBlocks(newBlocks);
  };

  const handleAddPreference = () => setPreferences([...preferences, { preferenceType: 'PREFERRED_TIME_RANGE', value: '', weight: '5' }]);
  const handlePreferenceChange = (index: number, field: string, value: string) => {
    const newPrefs = [...preferences];
    newPrefs[index] = { ...newPrefs[index], [field]: value };
    setPreferences(newPrefs);
  };
  const handleRemovePreference = (index: number) => {
    const newPrefs = [...preferences];
    newPrefs.splice(index, 1);
    setPreferences(newPrefs);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !meetingId) {
      setError('Email is required to save preferences');
      return;
    }
    
    setSubmitLoading(true);
    setError(null);
    setSuccess('');

    try {
      // Save all valid busy blocks
      const validBusyBlocks = busyBlocks.filter(b => b.busyStart && b.busyEnd);
      for (const block of validBusyBlocks) {
        await api.addScheduleRule(meetingId, {
          email,
          meetingId,
          busyStart: new Date(block.busyStart).toISOString(),
          busyEnd: new Date(block.busyEnd).toISOString()
        });
      }

      // Save all valid preferences
      const validPrefs = preferences.filter(p => p.value);
      for (const pref of validPrefs) {
        await api.addPreferenceRule(meetingId, {
          email,
          meetingId,
          preferenceType: pref.preferenceType,
          value: pref.value,
          weight: parseInt(pref.weight)
        });
      }

      setSuccess('Preferences and schedule saved successfully!');
      setTimeout(() => {
        navigate(`/meeting/${meetingId}/result`);
      }, 1500);
      
    } catch (err: any) {
      setError(err.message || 'Failed to save rules');
    } finally {
      setSubmitLoading(false);
    }
  };

  if (loading) return <div className="app-container"><div className="loading-state"><div className="spinner"></div><p>Loading meeting details...</p></div></div>;
  if (!meeting) return <div className="app-container"><div className="message-banner error">Meeting not found.</div></div>;

  return (
    <div className="app-container">
      <div className="preferences-page">
        <div className="page-header">
          <h1>Set Preferences</h1>
          <p className="page-desc">Define your constraints for: <strong>{meeting.title}</strong></p>
        </div>
        
        {error && <div className="message-banner error">{error}</div>}
        {success && <div className="message-banner success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Your Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required readOnly={!!initialEmail} />
          </div>

          <div className="section-block">
            <h3>Busy Blocks</h3>
            <p className="section-desc">Hard constraints: Times you are absolutely unavailable.</p>
            
            <div className="rule-list">
              {busyBlocks.map((block, index) => (
                <div key={index} className="rule-row">
                  <div>
                    <label>Start</label>
                    <input type="datetime-local" value={block.busyStart} onChange={(e) => handleBusyBlockChange(index, 'busyStart', e.target.value)} />
                  </div>
                  <div>
                    <label>End</label>
                    <input type="datetime-local" value={block.busyEnd} onChange={(e) => handleBusyBlockChange(index, 'busyEnd', e.target.value)} />
                  </div>
                  <button type="button" onClick={() => handleRemoveBusyBlock(index)} className="btn-icon" title="Remove block">✕</button>
                </div>
              ))}
            </div>
            <button type="button" onClick={handleAddBusyBlock} className="btn-secondary btn-sm">Add Busy Block</button>
          </div>

          <div className="section-block">
            <h3>Soft Preferences</h3>
            <p className="section-desc">Times you prefer or want to avoid. The algorithm optimizes for these.</p>
            
            <div className="rule-list">
              {preferences.map((pref, index) => (
                <div key={index} className="rule-row preference-row">
                  <div>
                    <label>Type</label>
                    <select value={pref.preferenceType} onChange={(e) => handlePreferenceChange(index, 'preferenceType', e.target.value)}>
                      <option value="PREFERRED_DAY">Preferred Day</option>
                      <option value="PREFERRED_TIME_RANGE">Time Range</option>
                      <option value="AVOID_BEFORE">Avoid Before</option>
                      <option value="AVOID_AFTER">Avoid After</option>
                    </select>
                  </div>
                  <div>
                    <label>Value</label>
                    <input type="text" value={pref.value} onChange={(e) => handlePreferenceChange(index, 'value', e.target.value)} placeholder="e.g. MONDAY or 09:00" />
                  </div>
                  <div className="weight-control">
                    <label>Weight: {pref.weight}</label>
                    <input type="range" min="1" max="10" value={pref.weight} onChange={(e) => handlePreferenceChange(index, 'weight', e.target.value)} />
                  </div>
                  <button type="button" onClick={() => handleRemovePreference(index)} className="btn-icon" title="Remove preference">✕</button>
                </div>
              ))}
            </div>
            <button type="button" onClick={handleAddPreference} className="btn-secondary btn-sm">Add Preference</button>
          </div>

          <div className="form-actions">
            <button type="submit" disabled={submitLoading} className="btn-primary">
              {submitLoading ? 'Saving...' : 'Save & Continue'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
