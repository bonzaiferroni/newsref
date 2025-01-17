FeedTable
-url
-selector
-external
-createdAt

LeadJobTable
*feedId < FeedTable
*leadId < LeadTable < SourceTable
-headline
-isExternal
-freshAt

LeadTable
*hostId < HostTable
*sourceId < SourceTable
-url

LeadResultTable
-result
-attemptedAt
-strategy

LeadInfo
*id > LeadTable
*targetId > SourceTable
*hostId > HostTable
-url
-feedHeadline
-lastAttemptAt
-isExternal
-freshAt
-linkCount

SourceTable
*hostId < HostTable
*noteId < NoteTable
-url
-title
-score
-type
-imageUrl
-thumbnail
-embed
-contentCount
-embedding
-seenAt
-accessedAt
-publishedAt

SourceInfo
*sourceId
-url
-pageTitle
-headline
-description
-hostCore
-hostName
-hostLogo
-wordCount
-section
-image
-thumbnail
-note
-seenAt
-score
-publishedAt
