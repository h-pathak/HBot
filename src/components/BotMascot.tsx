import React, { useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { BotId, MascotState } from '../types/agent';

export interface BotMascotProps {
  botId: BotId;
  state?: MascotState;
  size?: number;
  className?: string;
  showBadge?: boolean;
}

interface Palette {
  primary: string;
  light: string;
  dark: string;
  accent: string;
  aura: string;
}

const PALETTES: Record<BotId, Palette> = {
  atlas: {
    primary: '#F59E0B',
    light: '#FEF3C7',
    dark: '#B45309',
    accent: '#D97706',
    aura: 'rgba(245, 158, 11, 0.25)',
  },
  reactor: {
    primary: '#10B981',
    light: '#D1FAE5',
    dark: '#047857',
    accent: '#059669',
    aura: 'rgba(16, 185, 129, 0.28)',
  },
  helix: {
    primary: '#8B5CF6',
    light: '#EDE9FE',
    dark: '#6D28D9',
    accent: '#7C3AED',
    aura: 'rgba(139, 92, 246, 0.25)',
  },
  pulse: {
    primary: '#F43F5E',
    light: '#FFE4E6',
    dark: '#BE123C',
    accent: '#E11D48',
    aura: 'rgba(244, 63, 94, 0.25)',
  },
  forge: {
    primary: '#0EA5E9',
    light: '#E0F2FE',
    dark: '#0369A1',
    accent: '#0284C7',
    aura: 'rgba(14, 165, 233, 0.25)',
  },
};

export const BotMascot: React.FC<BotMascotProps> = ({
  botId,
  state = 'idle',
  size = 48,
  className = '',
  showBadge = true,
}) => {
  const palette = useMemo(() => PALETTES[botId] || PALETTES.atlas, [botId]);

  // Dynamic state motion variants
  const mascotVariants = {
    idle: {
      y: [0, -4, 0],
      rotate: [0, 0.8, -0.8, 0],
      scale: [1, 1.02, 1],
      transition: { duration: 3.2, repeat: Infinity, ease: 'easeInOut' },
    },
    listening: {
      y: [0, -2, 0],
      rotate: [-3, 3, -3],
      scale: [1.02, 1.05, 1.02],
      transition: { duration: 1.6, repeat: Infinity, ease: 'easeInOut' },
    },
    thinking: {
      y: [0, -3, 0],
      rotate: [-2, 2, -2],
      scale: [0.98, 1.03, 0.98],
      transition: { duration: 2.2, repeat: Infinity, ease: 'easeInOut' },
    },
    working: {
      y: [0, -5, 1, -4, 0],
      rotate: [-1.5, 1.5, -1.5],
      scale: [1, 1.04, 1],
      transition: { duration: 0.9, repeat: Infinity, ease: 'easeInOut' },
    },
    waiting_approval: {
      y: [0, -2, 0],
      rotate: [0, 1.5, 0],
      scale: [1, 1.03, 1],
      transition: { duration: 2.4, repeat: Infinity, ease: 'easeInOut' },
    },
    celebrating: {
      y: [0, -10, 0, -8, 0],
      rotate: [-6, 6, -4, 4, 0],
      scale: [1, 1.12, 1.02, 1.08, 1],
      transition: { duration: 1.2, repeat: Infinity, ease: 'easeInOut' },
    },
  };

  return (
    <div
      className={`relative inline-flex items-center justify-center select-none ${className}`}
      style={{ width: size, height: size }}
      role="img"
      aria-label={`${botId} mascot in ${state} state`}
    >
      {/* Ambient Bioluminescent Aura Glow */}
      <motion.div
        className="absolute inset-0 rounded-full filter blur-md -z-10"
        style={{ backgroundColor: palette.aura }}
        animate={{
          scale: state === 'celebrating' ? [1, 1.35, 1] : [1, 1.15, 1],
          opacity: state === 'thinking' ? [0.4, 0.8, 0.4] : [0.35, 0.65, 0.35],
        }}
        transition={{ duration: 2.6, repeat: Infinity, ease: 'easeInOut' }}
      />

      {/* Main Mascot Character Container */}
      <motion.svg
        viewBox="0 0 100 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="w-full h-full overflow-visible"
        variants={mascotVariants}
        animate={state}
      >
        <defs>
          {/* Radial soft highlights */}
          <radialGradient id={`glow-${botId}`} cx="38%" cy="32%" r="65%">
            <stop offset="0%" stopColor="#FFFFFF" stopOpacity="0.85" />
            <stop offset="45%" stopColor={palette.light} stopOpacity="0.95" />
            <stop offset="100%" stopColor={palette.primary} />
          </radialGradient>

          <linearGradient id={`grad-${botId}`} x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor={palette.light} />
            <stop offset="100%" stopColor={palette.primary} />
          </linearGradient>

          <filter id={`shadow-${botId}`} x="-20%" y="-20%" width="140%" height="140%">
            <feDropShadow dx="0" dy="3" stdDeviation="3" floodColor={palette.dark} floodOpacity="0.25" />
          </filter>
        </defs>

        {/* Render Character Mascot according to botId */}
        {botId === 'atlas' && <AtlasCharacter state={state} palette={palette} />}
        {botId === 'reactor' && <ReactorCharacter state={state} palette={palette} />}
        {botId === 'helix' && <HelixCharacter state={state} palette={palette} />}
        {botId === 'pulse' && <PulseCharacter state={state} palette={palette} />}
        {botId === 'forge' && <ForgeCharacter state={state} palette={palette} />}

        {/* Celebrating Sparkles Overlay */}
        {state === 'celebrating' && <CelebrationParticles palette={palette} />}
      </motion.svg>

      {/* State Badge Indicator */}
      {showBadge && (
        <span
          className="absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-white dark:border-[#0F172A] shadow-sm"
          style={{
            backgroundColor:
              state === 'idle'
                ? '#94A3B8'
                : state === 'thinking'
                ? '#8B5CF6'
                : state === 'working'
                ? '#10B981'
                : state === 'waiting_approval'
                ? '#F59E0B'
                : state === 'celebrating'
                ? '#EC4899'
                : '#0EA5E9',
          }}
          title={`Status: ${state}`}
        />
      )}
    </div>
  );
};

// ==========================================
// 1. ATLAS (Chief of Staff & Dispatcher)
// Golden star/compass sprite with round glasses & floating halo
// ==========================================
const AtlasCharacter: React.FC<{ state: MascotState; palette: Palette }> = ({ state, palette }) => {
  return (
    <g filter={`url(#shadow-atlas)`}>
      {/* Floating Rotating Celestial Halo */}
      <motion.ellipse
        cx="50"
        cy="20"
        rx="24"
        ry="6"
        stroke={palette.primary}
        strokeWidth="3"
        strokeDasharray="6 3"
        fill="none"
        animate={{
          rotate: [0, 360],
          y: state === 'thinking' ? [-2, 2, -2] : [0, -1, 0],
        }}
        transition={{
          rotate: { duration: 10, repeat: Infinity, ease: 'linear' },
          y: { duration: 2, repeat: Infinity, ease: 'easeInOut' },
        }}
        style={{ transformOrigin: '50px 20px' }}
      />

      {/* Main Star / Orb Body */}
      <motion.circle cx="50" cy="54" r="32" fill={`url(#glow-atlas)`} stroke={palette.dark} strokeWidth="1.5" />

      {/* Golden Compass Crown Star Point on Forehead */}
      <polygon points="50,26 53,35 62,35 55,40 58,49 50,44 42,49 45,40 38,35 47,35" fill="#FFFFFF" opacity="0.9" />

      {/* Round Glasses */}
      <g stroke="#1E293B" strokeWidth="2.4" fill="rgba(255,255,255,0.4)">
        {/* Left Lens */}
        <circle cx="39" cy="53" r="10" />
        {/* Right Lens */}
        <circle cx="61" cy="53" r="10" />
        {/* Bridge */}
        <line x1="49" y1="53" x2="51" y2="53" />
        {/* Temples */}
        <line x1="29" y1="52" x2="21" y2="50" />
        <line x1="71" y1="52" x2="79" y2="50" />
      </g>

      {/* Glasses Glint */}
      <path d="M34 47 L41 47" stroke="#FFFFFF" strokeWidth="1.5" strokeLinecap="round" />
      <path d="M56 47 L63 47" stroke="#FFFFFF" strokeWidth="1.5" strokeLinecap="round" />

      {/* Eyes Behind Glasses */}
      {state === 'celebrating' ? (
        <g stroke="#1E293B" strokeWidth="2.5" strokeLinecap="round">
          <path d="M35 54 Q39 49 43 54" fill="none" />
          <path d="M57 54 Q61 49 65 54" fill="none" />
        </g>
      ) : (
        <motion.g
          animate={state === 'thinking' ? { x: [-2, 2, -2] } : { scaleY: [1, 0.1, 1] }}
          transition={
            state === 'thinking'
              ? { duration: 1.5, repeat: Infinity }
              : { duration: 3.5, repeat: Infinity, times: [0, 0.05, 0.1] }
          }
          style={{ transformOrigin: '50px 53px' }}
        >
          <circle cx="40" cy="53" r="3.2" fill="#1E293B" />
          <circle cx="62" cy="53" r="3.2" fill="#1E293B" />
          <circle cx="41.2" cy="51.5" r="1.2" fill="#FFFFFF" />
          <circle cx="63.2" cy="51.5" r="1.2" fill="#FFFFFF" />
        </motion.g>
      )}

      {/* Cute Smile / Mouth */}
      <path
        d={state === 'celebrating' ? 'M44 64 Q50 72 56 64' : 'M45 65 Q50 69 55 65'}
        stroke="#1E293B"
        strokeWidth="2"
        strokeLinecap="round"
        fill={state === 'celebrating' ? '#F43F5E' : 'none'}
      />

      {/* Soft Rosy Cheeks */}
      <circle cx="30" cy="62" r="4" fill="#F43F5E" opacity="0.3" />
      <circle cx="70" cy="62" r="4" fill="#F43F5E" opacity="0.3" />
    </g>
  );
};

// ==========================================
// 2. REACTOR (NocVita Ops & Photobioreactor)
// Bioluminescent mint-emerald Erlenmeyer flask with rising glowing bubbles
// ==========================================
const ReactorCharacter: React.FC<{ state: MascotState; palette: Palette }> = ({ state, palette }) => {
  return (
    <g filter={`url(#shadow-reactor)`}>
      {/* Flask Neck Rim */}
      <rect x="42" y="16" width="16" height="5" rx="2.5" fill={palette.dark} />

      {/* Flask Glass Body Shape */}
      <path
        d="M44 20 L44 32 L20 74 C18 78 21 82 26 82 L74 82 C79 82 82 78 80 74 L56 32 L56 20 Z"
        fill="rgba(209, 250, 229, 0.4)"
        stroke={palette.dark}
        strokeWidth="2"
      />

      {/* Bioluminescent Liquid Fill with Animated Wave */}
      <clipPath id="flask-liquid-clip">
        <path d="M44 20 L44 32 L20 74 C18 78 21 82 26 82 L74 82 C79 82 82 78 80 74 L56 32 L56 20 Z" />
      </clipPath>

      <g clipPath="url(#flask-liquid-clip)">
        {/* Liquid Base */}
        <rect x="15" y="48" width="70" height="36" fill={`url(#glow-reactor)`} opacity="0.9" />

        {/* Animated Bioluminescent Bubbles */}
        <motion.circle
          cx="38"
          cy="74"
          r="3"
          fill="#FFFFFF"
          opacity="0.8"
          animate={{ y: [-2, -26], opacity: [0.8, 0] }}
          transition={{ duration: 1.8, repeat: Infinity, ease: 'easeOut' }}
        />
        <motion.circle
          cx="52"
          cy="76"
          r="4"
          fill="#D1FAE5"
          opacity="0.9"
          animate={{ y: [-2, -28], opacity: [0.9, 0] }}
          transition={{ duration: 2.2, repeat: Infinity, delay: 0.6, ease: 'easeOut' }}
        />
        <motion.circle
          cx="62"
          cy="72"
          r="2.5"
          fill="#FFFFFF"
          opacity="0.7"
          animate={{ y: [-1, -22], opacity: [0.7, 0] }}
          transition={{ duration: 1.5, repeat: Infinity, delay: 1.1, ease: 'easeOut' }}
        />
      </g>

      {/* Anime Flask Face */}
      {state === 'celebrating' ? (
        <g stroke="#064E3B" strokeWidth="2.5" strokeLinecap="round">
          <path d="M37 56 Q42 50 47 56" fill="none" />
          <path d="M53 56 Q58 50 63 56" fill="none" />
        </g>
      ) : (
        <g>
          <circle cx="42" cy="56" r="3.2" fill="#064E3B" />
          <circle cx="58" cy="56" r="3.2" fill="#064E3B" />
          <circle cx="43.2" cy="54.5" r="1.2" fill="#FFFFFF" />
          <circle cx="59.2" cy="54.5" r="1.2" fill="#FFFFFF" />
        </g>
      )}

      {/* Happy Smile */}
      <path d="M46 64 Q50 68 54 64" stroke="#064E3B" strokeWidth="2" strokeLinecap="round" fill="none" />

      {/* Glass Highlight Glint */}
      <path d="M28 72 L46 36" stroke="#FFFFFF" strokeWidth="2" strokeLinecap="round" opacity="0.5" />
    </g>
  );
};

// ==========================================
// 3. HELIX (Microbiology Researcher)
// Pastel violet double helix / microbe sprite with orbiting molecular dots
// ==========================================
const HelixCharacter: React.FC<{ state: MascotState; palette: Palette }> = ({ state, palette }) => {
  return (
    <g filter={`url(#shadow-helix)`}>
      {/* Orbiting Molecular Ring */}
      <motion.ellipse
        cx="50"
        cy="52"
        rx="36"
        ry="14"
        stroke={palette.primary}
        strokeWidth="1.5"
        strokeDasharray="4 3"
        fill="none"
        animate={{ rotate: [0, 360] }}
        transition={{ duration: 8, repeat: Infinity, ease: 'linear' }}
        style={{ transformOrigin: '50px 52px' }}
      />

      {/* Orbiting Electrons */}
      <motion.circle
        cx="14"
        cy="52"
        r="3.5"
        fill="#A78BFA"
        stroke="#FFFFFF"
        strokeWidth="1"
        animate={{ scale: [1, 1.3, 1] }}
        transition={{ duration: 1.5, repeat: Infinity }}
      />
      <motion.circle
        cx="86"
        cy="52"
        r="3.5"
        fill="#DDD6FE"
        stroke="#FFFFFF"
        strokeWidth="1"
        animate={{ scale: [1.3, 1, 1.3] }}
        transition={{ duration: 1.5, repeat: Infinity }}
      />

      {/* Main Microbe Body with cute squishy shape */}
      <motion.path
        d="M50 24 C68 24 80 36 80 54 C80 72 68 80 50 80 C32 80 20 72 20 54 C20 36 32 24 50 24 Z"
        fill={`url(#glow-helix)`}
        stroke={palette.dark}
        strokeWidth="1.5"
      />

      {/* Tiny Lab Scientist Ribbon / Badge */}
      <polygon points="68,64 78,64 73,74" fill="#F43F5E" />
      <circle cx="73" cy="62" r="4" fill="#FBBF24" stroke="#D97706" strokeWidth="1" />

      {/* Big Inquisitive Eyes */}
      <motion.g
        animate={
          state === 'thinking'
            ? { y: [-1.5, 0, -1.5], x: [1, -1, 1] }
            : { scaleY: [1, 0.1, 1] }
        }
        transition={
          state === 'thinking'
            ? { duration: 2, repeat: Infinity }
            : { duration: 4, repeat: Infinity, times: [0, 0.05, 0.1] }
        }
        style={{ transformOrigin: '50px 50px' }}
      >
        <ellipse cx="41" cy="48" rx="4.5" ry="6" fill="#3B0764" />
        <ellipse cx="59" cy="48" rx="4.5" ry="6" fill="#3B0764" />
        <circle cx="42.5" cy="45.5" r="2" fill="#FFFFFF" />
        <circle cx="60.5" cy="45.5" r="2" fill="#FFFFFF" />
        <circle cx="39.5" cy="51" r="1" fill="#FFFFFF" />
        <circle cx="57.5" cy="51" r="1" fill="#FFFFFF" />
      </motion.g>

      {/* Smile */}
      <path d="M46 60 Q50 64 54 60" stroke="#3B0764" strokeWidth="2" strokeLinecap="round" fill="none" />
      <circle cx="32" cy="56" r="3" fill="#C084FC" opacity="0.4" />
      <circle cx="68" cy="56" r="3" fill="#C084FC" opacity="0.4" />
    </g>
  );
};

// ==========================================
// 4. PULSE (Content Studio & Audio)
// Coral-rose cloud mascot wearing chunky DJ headphones
// ==========================================
const PulseCharacter: React.FC<{ state: MascotState; palette: Palette }> = ({ state, palette }) => {
  return (
    <g filter={`url(#shadow-pulse)`}>
      {/* Cloud Puff Body */}
      <path
        d="M32 68 C24 68 18 62 18 54 C18 47 23 41 30 40 C32 30 41 24 50 24 C60 24 68 31 70 40 C77 41 82 47 82 54 C82 62 76 68 68 68 Z"
        fill={`url(#glow-pulse)`}
        stroke={palette.dark}
        strokeWidth="1.5"
      />

      {/* Headphone Arch Band */}
      <path d="M22 52 C22 28 78 28 78 52" stroke="#1E293B" strokeWidth="4" strokeLinecap="round" fill="none" />

      {/* Headphone Ear Cushions */}
      <rect x="15" y="44" width="8" height="18" rx="4" fill="#1E293B" stroke="#0F172A" strokeWidth="1" />
      <rect x="77" y="44" width="8" height="18" rx="4" fill="#1E293B" stroke="#0F172A" strokeWidth="1" />
      <rect x="17" y="47" width="4" height="12" rx="2" fill={palette.light} />
      <rect x="79" y="47" width="4" height="12" rx="2" fill={palette.light} />

      {/* Animated Rhythmic Music Sparkle / Audio Waves */}
      <motion.path
        d="M86 36 Q90 32 94 36"
        stroke={palette.primary}
        strokeWidth="2"
        strokeLinecap="round"
        fill="none"
        animate={{ opacity: [0.2, 1, 0.2], y: [0, -3, 0] }}
        transition={{ duration: 1.2, repeat: Infinity }}
      />
      <motion.path
        d="M89 30 Q93 26 97 30"
        stroke={palette.primary}
        strokeWidth="2"
        strokeLinecap="round"
        fill="none"
        animate={{ opacity: [0.2, 1, 0.2], y: [0, -4, 0] }}
        transition={{ duration: 1.2, repeat: Infinity, delay: 0.3 }}
      />

      {/* Eyes bouncing to beat */}
      {state === 'celebrating' ? (
        <g stroke="#881337" strokeWidth="2.5" strokeLinecap="round">
          <path d="M38 48 Q42 42 46 48" fill="none" />
          <path d="M54 48 Q58 42 62 48" fill="none" />
        </g>
      ) : (
        <g>
          <circle cx="42" cy="48" r="3.2" fill="#881337" />
          <circle cx="58" cy="48" r="3.2" fill="#881337" />
          <circle cx="43.2" cy="46.5" r="1.2" fill="#FFFFFF" />
          <circle cx="59.2" cy="46.5" r="1.2" fill="#FFFFFF" />
        </g>
      )}

      {/* Cheerful Open Mouth */}
      <path d="M46 56 Q50 63 54 56 Z" fill="#881337" stroke="#881337" strokeWidth="1" />
      <path d="M48 59 Q50 61 52 59" stroke="#FFA4B8" strokeWidth="1" fill="none" />
    </g>
  );
};

// ==========================================
// 5. FORGE (Antigravity Coder & Tech Systems)
// Sky-cyan rounded robot-cube companion with pixel eyes
// ==========================================
const ForgeCharacter: React.FC<{ state: MascotState; palette: Palette }> = ({ state, palette }) => {
  return (
    <g filter={`url(#shadow-forge)`}>
      {/* Robot Antenna with Blinking Terminal LED */}
      <line x1="50" y1="26" x2="50" y2="16" stroke="#0369A1" strokeWidth="2.5" strokeLinecap="round" />
      <motion.circle
        cx="50"
        cy="14"
        r="3.5"
        fill="#38BDF8"
        animate={{ opacity: [0.4, 1, 0.4] }}
        transition={{ duration: 1, repeat: Infinity }}
      />

      {/* Main Cube Robot Body */}
      <rect x="22" y="26" width="56" height="52" rx="14" fill={`url(#glow-forge)`} stroke={palette.dark} strokeWidth="2" />

      {/* Dark Visor Screen */}
      <rect x="28" y="34" width="44" height="26" rx="8" fill="#0B132B" stroke="#1E293B" strokeWidth="1" />

      {/* Pixel Visor Eyes based on state */}
      {state === 'celebrating' ? (
        // Happy Anime Pixel Chevrons: ^ ^
        <g stroke="#38BDF8" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M36 48 L41 43 L46 48" fill="none" />
          <path d="M54 48 L59 43 L64 48" fill="none" />
        </g>
      ) : state === 'working' ? (
        // Focused Terminal Syntax Chevrons: > <
        <g stroke="#38BDF8" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M37 44 L43 47 L37 50" fill="none" />
          <path d="M63 44 L57 47 L63 50" fill="none" />
        </g>
      ) : (
        // Standard Glowing Pixel Eye Squares
        <g fill="#38BDF8">
          <rect x="37" y="42" width="7" height="9" rx="1.5" />
          <rect x="56" y="42" width="7" height="9" rx="1.5" />
          <rect x="38" y="43" width="2" height="2" fill="#FFFFFF" />
          <rect x="57" y="43" width="2" height="2" fill="#FFFFFF" />
        </g>
      )}

      {/* Mini Holographic Wrench / Tool floating on side */}
      <motion.path
        d="M74 38 L84 28 M81 25 L87 31"
        stroke="#38BDF8"
        strokeWidth="2.5"
        strokeLinecap="round"
        animate={{ y: [0, -3, 0], rotate: [0, 8, 0] }}
        transition={{ duration: 2, repeat: Infinity, ease: 'easeInOut' }}
        style={{ transformOrigin: '80px 30px' }}
      />

      {/* Metallic Chest Vent / Bolts */}
      <circle cx="34" cy="68" r="2" fill="#0369A1" />
      <circle cx="66" cy="68" r="2" fill="#0369A1" />
      <rect x="42" y="66" width="16" height="4" rx="2" fill="#0369A1" />
    </g>
  );
};

// ==========================================
// CELEBRATION PARTICLES
// ==========================================
const CelebrationParticles: React.FC<{ palette: Palette }> = ({ palette }) => {
  return (
    <g>
      {[
        { cx: 12, cy: 25, color: '#FBBF24', size: 2.5 },
        { cx: 88, cy: 22, color: '#EC4899', size: 3 },
        { cx: 20, cy: 78, color: '#34D399', size: 2 },
        { cx: 82, cy: 76, color: '#60A5FA', size: 2.5 },
        { cx: 50, cy: 6, color: palette.primary, size: 3.5 },
      ].map((p, i) => (
        <motion.circle
          key={i}
          cx={p.cx}
          cy={p.cy}
          r={p.size}
          fill={p.color}
          animate={{
            scale: [0, 1.4, 0],
            opacity: [0, 1, 0],
            y: [-2, -8],
          }}
          transition={{
            duration: 1.2,
            repeat: Infinity,
            delay: i * 0.2,
            ease: 'easeOut',
          }}
        />
      ))}
    </g>
  );
};

export default BotMascot;
