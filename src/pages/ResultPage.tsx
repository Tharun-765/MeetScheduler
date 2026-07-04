import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../services/api';
import './ResultPage.css';

export default function ResultPage() {
  const { meetingId } = useParams();
  
  const [meeting, setMeeting] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [negotiating, setNegotiating] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  // Time window for searching - hardcoded next 7 days for demo purposes
  const getSearchWindows = () => {
    const now = new Date();
    const start = new Date(now);
    start.setHours(9, 0, 0, 0); // start at 9am today
    
    const end = new Date(now);
    end.setDate(end.getDate() + 7);
    end.setHours(17, 0, 0, 0); // end at 5pm next week
    
    return {
      searchStart: start.toISOString(),
      searchEnd: end.toISOString()
    };
  };

  useEffect(() => {
    const fetchMeeting = async () => {
      if (!meetingId) return;
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

  const handleNegotiate = async () => {
    if (!meetingId) return;
    setNegotiating(true);
    setError(null);
    setResult(null);
    
    try {
      const response = await api.negotiateMeeting(meetingId, getSearchWindows());
      setResult(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to negotiate meeting time');
    } finally {
      setNegotiating(false);
    }
  };

  if (loading) return <div className="app-container"><div className="loading-state"><div className="spinner"></div><p>Loading meeting status...</p></div></div>;
  if (!meeting) return <div className="app-container"><div className="message-banner error">Meeting not found.</div></div>;

  return (
    <div className="app-container">
      <div className="result-page">
        <div className="result-header">
          <h1>Negotiation Result</h1>
          <p className="page-desc">{meeting.title}</p>
        </div>
        
        {!result && !negotiating && (
          <div className="action-panel">
            <p>All participants have submitted their preferences. Ready to find the optimal time?</p>
            <button onClick={handleNegotiate} className="btn-primary btn-large">
              Run Scheduling Engine
            </button>
          </div>
        )}

        {negotiating && (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Crunching preferences and schedule constraints...</p>
          </div>
        )}

        {error && <div className="message-banner error">{error}</div>}

        {result && (
          <div className="result-card-container">
            {result.proposedStart ? (
              <>
                <h2>Optimal Time Selected</h2>
                <div className="time-hero">
                  <div className="time-block">
                    <span className="time-label">Starts</span>
                    <span className="time-value">
                      {new Date(result.proposedStart).toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })} at {new Date(result.proposedStart).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                  <div className="time-block">
                    <span className="time-label">Ends</span>
                    <span className="time-value">
                      {new Date(result.proposedEnd).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                </div>
                
                <div className="match-confidence">
                  <div className="match-confidence-fill"></div>
                </div>

                <div className="justification-block">
                  <h4>Engine Justification</h4>
                  <p>{result.justificationString}</p>
                </div>
              </>
            ) : (
              <>
                <h2>No Valid Slot Found</h2>
                <div className="justification-block">
                  <p>{result.message || result.justificationString || 'No available slot found that satisfies all hard constraints.'}</p>
                </div>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
