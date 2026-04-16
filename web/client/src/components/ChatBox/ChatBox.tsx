import { useEffect, useRef, useState } from 'react';
import type { ChatMessage } from '@muw/shared';

export function ChatBox({ messages, onSend }: { messages: ChatMessage[]; onSend: (text: string) => void }) {
  const [text, setText] = useState('');
  const scrollRef = useRef<HTMLDivElement>(null);
  useEffect(() => { scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight }); }, [messages.length]);
  return (
    <div className="arcade-frame p-3 flex flex-col h-48 md:h-64">
      <div className="font-pixel text-[0.7rem] text-muwGold mb-1">Chat</div>
      <div ref={scrollRef} className="flex-1 overflow-y-auto thin-scroll font-vt text-sm space-y-0.5 pr-2">
        {messages.map(m => (
          <div key={m.id}><span className="font-pixel text-[0.7rem] text-muwGold">{m.username}:</span> {m.text}</div>
        ))}
      </div>
      <form
        className="mt-2 flex gap-2"
        onSubmit={(e) => { e.preventDefault(); if (text.trim()) { onSend(text.trim()); setText(''); } }}
      >
        <input
          className="flex-1 bg-black/60 border-2 border-muwSteelLight px-2 py-1 font-vt text-sm focus:outline-none focus:border-muwGold"
          value={text}
          onChange={(e) => { setText(e.target.value); }}
          maxLength={500}
          placeholder="Say something"
        />
        <button className="pixel-btn">Send</button>
      </form>
    </div>
  );
}
