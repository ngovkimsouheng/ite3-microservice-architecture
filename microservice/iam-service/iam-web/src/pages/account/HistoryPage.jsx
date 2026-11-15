import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
    History, ShieldAlert, CheckCircle2, XCircle,
    Clock, Globe, Monitor, Smartphone, Filter,
    KeyRound, UserCheck, Lock, Activity
} from 'lucide-react';
import CustomSelect from '../../components/CustomSelect';

export default function HistoryPage() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('login'); // 'login' | 'activity'
    const [statusFilter, setStatusFilter] = useState('all');

    // Login History Data (+5 pts)
    const loginHistory = [
        {
            id: 'log-1',
            status: 'SUCCESS',
            timestamp: '2026-11-15T00:30:12Z',
            ipAddress: '127.0.0.1 (Localhost)',
            device: 'Chrome 131 / Windows 11',
            location: 'Phnom Penh, Cambodia',
            method: 'Password'
        },
        {
            id: 'log-2',
            status: 'SUCCESS',
            timestamp: '2026-11-14T19:22:04Z',
            ipAddress: '110.74.214.15',
            device: 'Mobile Safari 18 / iOS',
            location: 'Siem Reap, Cambodia',
            method: 'Google SSO'
        },
        {
            id: 'log-3',
            status: 'FAILED',
            timestamp: '2026-11-14T15:10:45Z',
            ipAddress: '103.20.144.52',
            device: 'Chrome 130 / macOS',
            location: 'Phnom Penh, Cambodia',
            method: 'Password (Invalid credentials)'
        },
        {
            id: 'log-4',
            status: 'SUCCESS',
            timestamp: '2026-11-13T10:05:30Z',
            ipAddress: '127.0.0.1 (Localhost)',
            device: 'Chrome 130 / Windows 11',
            location: 'Phnom Penh, Cambodia',
            method: 'Password'
        }
    ];

    // Account Activity Data (+5 pts)
    const activities = [
        {
            id: 'act-1',
            event: i18n.language === 'en' ? 'Profile Details Updated' : 'បានកែប្រែព័ត៌មានផ្ទាល់ខ្លួន',
            description: i18n.language === 'en' ? 'Changed given name and language preferences' : 'បានកែប្រែនាមខ្លួន និងភាសាដែលពេញចិត្ត',
            timestamp: '2026-11-15T01:50:00Z',
            icon: UserCheck,
            color: 'text-[#253C95] bg-[#253C95]/10'
        },
        {
            id: 'act-2',
            event: i18n.language === 'en' ? 'Password Changed' : 'បានផ្លាស់ប្តូរពាក្យសម្ងាត់',
            description: i18n.language === 'en' ? 'Successfully changed account password' : 'បានផ្លាស់ប្តូរពាក្យសម្ងាត់គណនីដោយជោគជ័យ',
            timestamp: '2026-11-14T18:00:00Z',
            icon: KeyRound,
            color: 'text-emerald-600 bg-emerald-50'
        },
        {
            id: 'act-3',
            event: i18n.language === 'en' ? 'Google Account Linked' : 'បានភ្ជាប់គណនី Google',
            description: i18n.language === 'en' ? 'Connected Google single sign-on authentication' : 'បានភ្ជាប់ការចូលប្រើប្រាស់តាម Google SSO',
            timestamp: '2026-11-10T12:00:00Z',
            icon: Activity,
            color: 'text-[#253C95] bg-[#253C95]/10'
        },
        {
            id: 'act-4',
            event: i18n.language === 'en' ? 'Session Terminated' : 'បានបញ្ចប់សម័យការងារ',
            description: i18n.language === 'en' ? 'Terminated active session on MacBook Pro' : 'បានបញ្ចប់សម័យការងារលើ MacBook Pro',
            timestamp: '2026-11-09T08:30:00Z',
            icon: Lock,
            color: 'text-[#DF1F28] bg-[#DF1F28]/10'
        }
    ];

    const filteredLogins = loginHistory.filter((item) => {
        if (statusFilter === 'all') return true;
        return item.status.toLowerCase() === statusFilter.toLowerCase();
    });

    return (
        <div className="space-y-5">
            {/* Tab Selection */}
            <div className="bg-white p-2 rounded-2xl border border-slate-200/90 shadow-2xs flex items-center justify-between">
                <div className="flex items-center gap-1.5">
                    <button
                        type="button"
                        onClick={() => setActiveTab('login')}
                        className={`px-5 py-2.5 rounded-xl text-sm sm:text-base font-semibold transition-all cursor-pointer ${
                            activeTab === 'login'
                                ? 'bg-[#253C95] text-white shadow-sm'
                                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                        }`}
                    >
                        {i18n.language === 'en' ? 'Login History' : 'ប្រវត្តិការចូលប្រើ'}
                    </button>
                    <button
                        type="button"
                        onClick={() => setActiveTab('activity')}
                        className={`px-5 py-2.5 rounded-xl text-sm sm:text-base font-semibold transition-all cursor-pointer ${
                            activeTab === 'activity'
                                ? 'bg-[#253C95] text-white shadow-sm'
                                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                        }`}
                    >
                        {i18n.language === 'en' ? 'Security Activity' : 'សកម្មភាពសុវត្ថិភាព'}
                    </button>
                </div>

                {activeTab === 'login' && (
                    <div className="flex items-center gap-2 pr-2">
                        <Filter size={16} className="text-slate-400 shrink-0" />
                        <CustomSelect
                            value={statusFilter}
                            onChange={(val) => setStatusFilter(val)}
                            options={[
                                { value: 'all', label: i18n.language === 'en' ? 'All Status' : 'ទាំងអស់' },
                                { value: 'success', label: i18n.language === 'en' ? 'Successful' : 'ជោគជ័យ' },
                                { value: 'failed', label: i18n.language === 'en' ? 'Failed' : 'បរាជ័យ' }
                            ]}
                            className="w-40"
                        />
                    </div>
                )}
            </div>

            {/* TAB 1: LOGIN HISTORY TABLE */}
            {activeTab === 'login' && (
                <div className="bg-white rounded-3xl border border-slate-200/90 shadow-2xs overflow-hidden">
                    <div className="p-6 border-b border-slate-100">
                        <h3 className="text-base sm:text-lg font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Recent Login Attempts' : 'កំណត់ត្រាចូលប្រើប្រាស់ថ្មីៗ'}
                        </h3>
                        <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                            {i18n.language === 'en'
                                ? 'Review your sign-in history to detect any unauthorized access.'
                                : 'ពិនិត្យប្រវត្តិចូលប្រើប្រាស់ដើម្បីដឹងពីសកម្មភាពមិនប្រក្រតី។'}
                        </p>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-sm">
                            <thead className="bg-slate-50 text-slate-500 text-xs uppercase font-bold tracking-wider border-b border-slate-100">
                                <tr>
                                    <th className="px-6 py-4">{i18n.language === 'en' ? 'Status' : 'ស្ថានភាព'}</th>
                                    <th className="px-6 py-4">{i18n.language === 'en' ? 'Device & Browser' : 'ឧបករណ៍ & កម្មវិធីរុករក'}</th>
                                    <th className="px-6 py-4">{i18n.language === 'en' ? 'IP Address' : 'អាសយដ្ឋាន IP'}</th>
                                    <th className="px-6 py-4">{i18n.language === 'en' ? 'Location' : 'ទីតាំង'}</th>
                                    <th className="px-6 py-4">{i18n.language === 'en' ? 'Timestamp' : 'ពេលវេលា'}</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                {filteredLogins.map((row) => (
                                    <tr key={row.id} className="hover:bg-slate-50/60 transition-colors">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {row.status === 'SUCCESS' ? (
                                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                    <CheckCircle2 size={14} className="text-emerald-500" />
                                                    {i18n.language === 'en' ? 'Success' : 'ជោគជ័យ'}
                                                </span>
                                            ) : (
                                                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-[#DF1F28]/10 text-[#DF1F28] border border-[#DF1F28]/20">
                                                    <XCircle size={14} className="text-[#DF1F28]" />
                                                    {i18n.language === 'en' ? 'Failed' : 'បរាជ័យ'}
                                                </span>
                                            )}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-slate-900 font-semibold">
                                            {row.device}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap font-mono text-slate-600">
                                            {row.ipAddress}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-slate-600">
                                            {row.location}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-slate-500 font-mono text-xs">
                                            {new Date(row.timestamp).toLocaleString()}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* TAB 2: ACCOUNT ACTIVITY TIMELINE */}
            {activeTab === 'activity' && (
                <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200/90 shadow-2xs">
                    <div className="pb-6 border-b border-slate-100 mb-6">
                        <h3 className="text-base sm:text-lg font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Security & Account Activity Log' : 'កំណត់ហេតុសកម្មភាពគណនី'}
                        </h3>
                        <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                            {i18n.language === 'en'
                                ? 'Audit log of significant changes made to your account.'
                                : 'កំណត់ត្រានៃការផ្លាស់ប្តូរសំខាន់ៗដែលបានធ្វើឡើងលើគណនី។'}
                        </p>
                    </div>

                    <div className="space-y-6 relative before:absolute before:inset-0 before:left-5 before:w-0.5 before:bg-slate-100">
                        {activities.map((act) => {
                            const Icon = act.icon;
                            return (
                                <div key={act.id} className="relative flex items-start gap-4 pl-1">
                                    <div className={`w-10 h-10 rounded-2xl flex items-center justify-center shrink-0 z-10 ${act.color}`}>
                                        <Icon size={18} />
                                    </div>
                                    <div className="flex-1 bg-slate-50/70 p-4 rounded-2xl border border-slate-100/90">
                                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1">
                                            <h4 className="text-sm sm:text-base font-bold text-slate-900">
                                                {act.event}
                                            </h4>
                                            <span className="text-xs text-slate-400 font-mono flex items-center gap-1">
                                                <Clock size={13} />
                                                {new Date(act.timestamp).toLocaleString()}
                                            </span>
                                        </div>
                                        <p className="text-xs sm:text-sm text-slate-600 mt-1">
                                            {act.description}
                                        </p>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}

