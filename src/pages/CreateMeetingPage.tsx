import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import './CreateMeetingPage.css';

export default function CreateMeetingPage() {
  const [title, setTitle] = useState('');
  const [duration, setDuration] = useState(30);
  const [organizerEmail, setOrganizerEmail] = useState('');
  const [inviteeEmails, setInviteeEmails] = useState(['']);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const navigate = useNavigate();

  const handleAddInvitee = () => setInviteeEmails([...inviteeEmails, '']);
  const handleRemoveInvitee = (index: number) => {
    const newInvitees = [...inviteeEmails];
    newInvitees.splice(index, 1);
    setInviteeEmails(newInvitees);
  };
  const handleInviteeChange = (index: number, value: string) => {
    const newInvitees = [...inviteeEmails];
    newInvitees[index] = value;
    setInviteeEmails(newInvitees);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        title,
        durationMinutes: duration,
        organizerEmail,
        inviteeEmails: inviteeEmails.filter(e => e.trim() !== '')
      };
      
      const response = await api.createMeeting(payload);
      // Navigate to preference setting page for the organizer
      navigate(`/meeting/${response.data.id}/preferences?email=${encodeURIComponent(organizerEmail)}`);
    } catch (err: any) {
      setError(err.message || 'Failed to create meeting');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      <div className="create-meeting-page">
        <div className="page-header">
          <h1>Create a Meeting</h1>
          <p className="page-desc">Set up a new auto-negotiating meeting and invite participants.</p>
        </div>
        
        {error && <div className="message-banner error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Meeting Title</label>
            <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="e.g. Q3 Roadmap Planning" required />
          </div>
          
          <div className="form-group">
            <label>Duration (minutes)</label>
            <input type="number" min="15" step="15" value={duration} onChange={(e) => setDuration(parseInt(e.target.value))} required />
          </div>
          
          <div className="form-group">
            <label>Organizer Email</label>
            <input type="email" value={organizerEmail} onChange={(e) => setOrganizerEmail(e.target.value)} placeholder="you@company.com" required />
          </div>
          
          <div className="form-group">
            <label>Invitee Emails</label>
            <div className="invitee-list">
              {inviteeEmails.map((email, index) => (
                <div key={index} className="invitee-row">
                  <div className="input-wrapper">
                    <input 
                      type="email" 
                      value={email} 
                      onChange={(e) => handleInviteeChange(index, e.target.value)} 
                      placeholder="colleague@company.com"
                      required 
                    />
                  </div>
                  {inviteeEmails.length > 1 && (
                    <button type="button" onClick={() => handleRemoveInvitee(index)} className="btn-icon" title="Remove invitee">✕</button>
                  )}
                </div>
              ))}
            </div>
            <button type="button" onClick={handleAddInvitee} className="btn-secondary btn-sm">Add Another Invitee</button>
          </div>
          
          <div className="form-actions">
            <button type="submit" disabled={loading} className="btn-primary">
              {loading ? 'Creating...' : 'Create Meeting'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
