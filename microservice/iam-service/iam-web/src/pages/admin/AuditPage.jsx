import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    FileText, Smartphone, History, Shield, AlertTriangle,
    CheckCircle2, XCircle, Search, Filter, Trash2, RefreshCw,
    Laptop, Globe, Clock, User, LogOut, ArrowRight, Loader2,
    Monitor, Tablet
} from 'lucide-react';

export default function AuditPage() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('AUDIT_LOGS'); // 'AUDIT_LOGS' | 'LOGIN_HISTORY' | 'SESSIONS'
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [loading, setLoading] = useState(true);

    const [sessions, setSessions] = useState([]);
    const [loginHistory, setLoginHistory] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    const fetchAllData = async (silent = false) => {
        if (!silent) setLoading(true);
        try {
            const [auditData, loginData, sessionData] = await Promise.all([
                adminService.getEnterpriseSecurityActivity().catch(err => {
                    console.error('Failed to load enterprise security activity:', err);
                    return [];
                }),
                adminService.getEnterpriseLoginHistory(statusFilter).catch(err => {
                    console.error('Failed to load enterprise login history:', err);
                    return [];
                }),
                adminService.getEnterpriseSessions().catch(err => {
                    console.error('Failed to load enterprise sessions:', err);
                    return [];
                })
            ]);
            setAuditLogs(Array.isArray(auditData) ? auditData : []);
            setLoginHistory(Array.isArray(loginData) ? loginData : []);
            setSessions(Array.isArray(sessionData) ? sessionData : []);
        } finally {
            if (!silent) setLoading(false);
        }
    };

    const fetchAuditLogs = async () => {
        try {
            const data = await adminService.getEnterpriseSecurityActivity();
            setAuditLogs(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise security activity:', err);
            setAuditLogs([]);
        }
    };

    const fetchLoginHistory = async (status = 'ALL') => {
        try {
            const data = await adminService.getEnterpriseLoginHistory(status);
            setLoginHistory(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise login history:', err);
            setLoginHistory([]);
        }
    };

    const fetchSessions = async () => {
        try {
            const data = await adminService.getEnterpriseSessions();
            setSessions(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise sessions:', err);
            setSessions([]);
        }
    };

    // Load ALL data on mount so all tab counts display immediately
    useEffect(() => {
        fetchAllData();
    }, []);

    // Terminate individual session
    const handleTerminateSession = async (sessionId) => {
        try {
            await adminService.terminateEnterpriseSession(sessionId);
            setSessions(prev => prev.filter(s => s.sessionId !== sessionId));
            showToast('Session terminated successfully.');
        } catch (err) {
            showToast('Failed to terminate session.', 'error');
        }
    };

    const getDeviceIcon = (deviceStr = '') => {
        const d = deviceStr.toLowerCase();
        if (d.includes('mobile') || d.includes('iphone') || d.includes('android')) return Smartphone;
        if (d.includes('tablet') || d.includes('ipad')) return Tablet;
        if (d.includes('mac') || d.includes('laptop')) return Laptop;
        return Monitor;
    };

    const formatTimestamp = (ts) => {
        if (!ts) return '-';
        try {
            return new Date(ts).toLocaleString();
        } catch {
            return String(ts);
        }
    };

    // Filtered lists
    const filteredAuditLogs = auditLogs.filter(log => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.description?.toLowerCase().includes(q)) ||
               (log.details?.toLowerCase().includes(q)) ||
               (log.username?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q));
    });

    const filteredLoginHistory = loginHistory.filter(log => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.device?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q)) ||
               (log.location?.toLowerCase().includes(q)) ||
               (log.authMethod?.toLowerCase().includes(q));
    });

    const filteredSessions = sessions.filter(sess => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (sess.username?.toLowerCase().includes(q)) ||
               (sess.device?.toLowerCase().includes(q)) ||
               (sess.ipAddress?.toLowerCase().includes(q));
    });

    return (
        <div className="space-y-6">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header */}
            <div className="bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-lg sm:text-xl font-bold text-slate-900">
                        {i18n.language === 'en' ? 'Audit & Security Compliance' : 'ការត្រួតពិនិត្យ និងសុវត្ថិភាព'}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                        {i18n.language === 'en'
                            ? 'Real-time enterprise audit trail, sign-in attempts, and active user sessions.'
                            : 'កំណត់ត្រាសុវត្ថិភាពសហគ្រាស ការចូលប្រើប្រាស់ និងសម័យការងារសកម្ម។'}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => fetchAllData()}
                        className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                        title="Refresh all audit data"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Tabs & Search Controls */}
            <div className="bg-white p-3 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-1.5 p-1 bg-slate-100 rounded-xl w-fit flex-wrap">
                    <button
                        onClick={() => setActiveTab('AUDIT_LOGS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'AUDIT_LOGS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <FileText size={16} />
                        <span>{i18n.language === 'en' ? 'Audit Events' : 'កំណត់ហេតុសកម្មភាព'} ({auditLogs.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('LOGIN_HISTORY')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'LOGIN_HISTORY'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <History size={16} />
                        <span>{i18n.language === 'en' ? 'Sign-in Attempts' : 'ប្រវត្តិចូលប្រើ'} ({loginHistory.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('SESSIONS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer whitespace-nowrap ${
                            activeTab === 'SESSIONS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Smartphone size={16} />
                        <span>{i18n.language === 'en' ? 'Active Sessions' : 'សម័យការងារសកម្ម'} ({sessions.length})</span>
                    </button>
                </div>

                <div className="flex items-center gap-2">
                    <div className="relative flex-1 sm:w-72">
                        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                        <input
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            placeholder={i18n.language === 'en' ? 'Filter events, users, IP...' : 'ស្វែងរកតាមព្រឹត្តិការណ៍ អ្នកប្រើ ឬ IP...'}
                            className="w-full pl-10 pr-3.5 py-2 rounded-xl border border-slate-200 text-sm outline-none focus:border-[#253C95] bg-slate-50/50 hover:bg-white focus:bg-white"
                        />
                    </div>
                </div>
            </div>

            {/* Loading State */}
            {loading ? (
                <div className="bg-white p-14 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col items-center justify-center gap-2 text-slate-400">
                    <Loader2 size={32} className="animate-spin text-[#253C95]" />
                    <span className="text-sm font-medium">Loading audit data...</span>
                </div>
            ) : (
                <>
                    {/* --- TAB 1: SYSTEM AUDIT LOGS --- */}
                    {activeTab === 'AUDIT_LOGS' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredAuditLogs.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-sm font-medium">
                                    No audit events recorded yet.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-sm font-bold uppercase tracking-wider text-slate-500 whitespace-nowrap">
                                                <th className="px-5 py-3.5 whitespace-nowrap">Event Action</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Details / User</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Origin IP</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredAuditLogs.map(item => (
                                                <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                                                    <td className="px-5 py-3.5 whitespace-nowrap">
                                                        <span className="inline-block px-3 py-1 rounded-md text-sm font-bold uppercase font-mono bg-blue-50 text-[#253C95] border border-blue-200/60">
                                                            {item.description || item.category}
                                                        </span>
                                                    </td>
                                                    <td className="px-5 py-3.5 text-slate-700">
                                                        <p className="font-semibold text-slate-900 leading-tight">
                                                            {item.details || item.description}
                                                        </p>
                                                    </td>
                                                    <td className="px-5 py-3.5 font-mono text-slate-600 text-sm">
                                                        {item.ipAddress}
                                                    </td>
                                                    <td className="px-5 py-3.5 text-right text-sm font-mono text-slate-500 whitespace-nowrap">
                                                        {formatTimestamp(item.timestamp)}
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 2: GLOBAL LOGIN HISTORY --- */}
                    {activeTab === 'LOGIN_HISTORY' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredLoginHistory.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-sm font-medium">
                                    No login attempts found.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-sm font-bold uppercase tracking-wider text-slate-500 whitespace-nowrap">
                                                <th className="px-5 py-3.5 whitespace-nowrap">Status</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">Device & Method</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap">IP & Location</th>
                                                <th className="px-5 py-3.5 whitespace-nowrap text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredLoginHistory.map(log => (
                                                <tr key={log.id} className="hover:bg-slate-50/60 transition-colors">
                                                    <td className="px-5 py-3.5 whitespace-nowrap">
                                                        {log.status === 'SUCCESS' ? (
                                                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                <CheckCircle2 size={14} className="text-emerald-500" /> Success
                                                            </span>
                                                        ) : (
                                                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-semibold bg-red-50 text-[#DF1F28] border border-red-200">
                                                                <XCircle size={14} className="text-[#DF1F28]" /> Failed
                                                            </span>
                                                        )}
                                                    </td>
                                                    <td className="px-5 py-3.5">
                                                        <p className="font-semibold text-slate-900 leading-tight">{log.device}</p>
                                                        <span className="inline-block mt-1 px-2.5 py-0.5 rounded text-xs font-bold uppercase bg-slate-100 text-slate-600">
                                                            {log.authMethod || 'Password'}
                                                        </span>
                                                    </td>
                                                    <td className="px-5 py-3.5">
                                                        <p className="font-mono text-sm text-slate-700 font-medium">{log.ipAddress}</p>
                                                        <p className="text-sm text-slate-400 mt-0.5">{log.location}</p>
                                                    </td>
                                                    <td className="px-5 py-3.5 text-right text-sm font-mono text-slate-500 whitespace-nowrap">
                                                        {formatTimestamp(log.timestamp)}
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 3: ACTIVE ENTERPRISE SESSIONS --- */}
                    {activeTab === 'SESSIONS' && (
                        <div>
                            {filteredSessions.length === 0 ? (
                                <div className="bg-white p-10 rounded-2xl border border-slate-200/90 text-center text-slate-400 text-sm font-medium">
                                    No active user sessions currently stored in Redis.
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
                                    {filteredSessions.map(sess => {
                                        const DeviceIcon = getDeviceIcon(sess.device);
                                        return (
                                            <div key={sess.sessionId} className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200/90 shadow-2xs flex items-center justify-between gap-4">
                                                <div className="flex items-center gap-3.5 min-w-0">
                                                    <div className="w-11 h-11 rounded-xl bg-[#253C95]/10 text-[#253C95] flex items-center justify-center shrink-0">
                                                        <DeviceIcon size={22} />
                                                    </div>
                                                    <div className="min-w-0">
                                                        <div className="flex items-center gap-2">
                                                            <p className="text-base font-bold text-slate-900 truncate">
                                                                @{sess.username}
                                                            </p>
                                                            {sess.isCurrent && (
                                                                <span className="px-2.5 py-0.5 rounded text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                    Your Session
                                                                </span>
                                                            )}
                                                        </div>
                                                        <p className="text-sm text-slate-600 mt-0.5 truncate">{sess.device}</p>
                                                        <p className="text-sm text-slate-400 font-mono mt-0.5">
                                                            {sess.ipAddress} &bull; {sess.lastAccessedAt ? formatTimestamp(sess.lastAccessedAt) : 'Active now'}
                                                        </p>
                                                    </div>
                                                </div>
                                                {!sess.isCurrent && (
                                                    <button
                                                        onClick={() => handleTerminateSession(sess.sessionId)}
                                                        className="px-3.5 py-2 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-sm font-semibold transition-all cursor-pointer shrink-0"
                                                    >
                                                        Terminate
                                                    </button>
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
